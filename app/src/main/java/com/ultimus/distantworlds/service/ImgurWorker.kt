/*
 *  Copyright 2018 Chris Margonis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ultimus.distantworlds.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ultimus.distantworlds.BuildConfig
import com.ultimus.distantworlds.BuildConfig.DISTANT_WORLDS_AUTHORITY
import com.ultimus.distantworlds.model.AlbumResponse
import com.ultimus.distantworlds.model.Image
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

/**
 * Created by Chris Margonis on 03/11/2018.
 */
class ImgurWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        internal fun enqueueLoad() {
            val workManager = WorkManager.getInstance()
            workManager.enqueue(OneTimeWorkRequestBuilder<ImgurWorker>().build())
        }
    }

    private val tag = "ImgurWorker"

    override fun doWork(): Result {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code() in 500..599) {
                try {
                    throw RemoteMuzeiArtSource.RetryException()
                } catch (e: RemoteMuzeiArtSource.RetryException) {
                    e.printStackTrace()
                }
            }
            response
        }
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(interceptor)
        }
        val client = builder.build()

        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        val retrofit = Retrofit.Builder()
            .baseUrl(IMGUR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .client(client)
            .build()

        val service = retrofit.create(DistantWorldsService::class.java)
        val response = service.getAlbumDetails(
            BuildConfig.IMGUR_DW_ALBUM,
            BuildConfig.IMGUR_CLIENT_ID
        )
        val album: Response<AlbumResponse>?
        try {
            album = response.execute()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.RETRY
        }

        if (album == null || album.body()?.success == false) {
            return Result.RETRY
        }

        if (album.body()?.data == null) {
            if (BuildConfig.DEBUG) {
                Log.w(tag, "No photos returned from API.")
            }
            return Result.FAILURE
        }

        val photo: Image
        val random = Random()
        val imageToken: String
        val photosList = album.body()?.data?.images
        photo = photosList!![random.nextInt(photosList.size)]
        imageToken = photo.id

        val imageResponseCall =
            service.getSingleAlbumImage(
                BuildConfig.IMGUR_DW_ALBUM, photo.id,
                BuildConfig.IMGUR_CLIENT_ID
            )
        try {
            val img = imageResponseCall.execute()
            if (img?.body() != null && img.body()?.success == true) {
                val image = img.body()?.data
                ProviderContract.Artwork.addArtwork(
                    applicationContext,
                    DISTANT_WORLDS_AUTHORITY,
                    Artwork().apply {
                        this.token = imageToken
                        title = image?.title
                        byline = image?.description
                        persistentUri = Uri.parse(image?.link)
                    }
                )
            }

        } catch (e: IOException) {
            e.printStackTrace()
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}
