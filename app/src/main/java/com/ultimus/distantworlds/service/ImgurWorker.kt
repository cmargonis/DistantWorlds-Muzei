/*
 *  Copyright 2020 Chris Margonis
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
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ultimus.distantworlds.BuildConfig
import com.ultimus.distantworlds.BuildConfig.DISTANT_WORLDS_AUTHORITY
import com.ultimus.distantworlds.BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY
import com.ultimus.distantworlds.model.AlbumResponse
import com.ultimus.distantworlds.model.Image
import com.ultimus.distantworlds.provider.DistantWorldsSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.ArrayList

/**
 * Created by Chris Margonis on 03/11/2018.
 */
class ImgurWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {

        internal const val keySource: String = "imgur_source"
        internal fun enqueueLoad(source: DistantWorldsSource, context: Context?) {
            context ?: return
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder().putAll(mutableMapOf<String, Any>(keySource to source.name)).build()
            workManager.enqueue(OneTimeWorkRequestBuilder<ImgurWorker>().setInputData(data).build())
        }
    }

    override fun doWork(): Result {
        val configuration = WorkConfiguration.fromInput(inputData)
        val response = getRetrofit().getAlbumDetails(configuration.albumId, BuildConfig.IMGUR_CLIENT_ID)
        val album: Response<AlbumResponse>?
        try {
            album = response.execute()
        } catch (e: IOException) {
            Timber.e(e)
            return Result.retry()
        }

        if (album == null || album.body()?.success == false) {
            return Result.retry()
        }

        val photosList = album.body()?.data?.images
        if (photosList.isNullOrEmpty()) {
            Timber.w("No photos returned from API.")
            return Result.failure()
        }

        postArtworkToMuzei(configuration.source, photosList)
        return Result.success()
    }

    private fun postArtworkToMuzei(source: DistantWorldsSource, photosList: ArrayList<Image>) {
        val authority = when (source) {
            DistantWorldsSource.DISTANT_WORLDS_1 -> DISTANT_WORLDS_AUTHORITY
            DistantWorldsSource.DISTANT_WORLDS_2 -> DISTANT_WORLDS_TWO_AUTHORITY
        }
        val providerClient = ProviderContract.getProviderClient(applicationContext, authority)
        providerClient.addArtwork(photosList.map { image ->
            Artwork(
                token = image.id,
                title = image.title,
                byline = image.description,
                webUri = Uri.parse(image.link),
                persistentUri = Uri.parse(image.link)
            )
        }.shuffled())
    }

    private fun getRetrofit(): DistantWorldsService {
        val client = getHttpClient()
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        val retrofit = Retrofit.Builder()
            .baseUrl(IMGUR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .client(client)
            .build()
        return retrofit.create(DistantWorldsService::class.java)
    }

    private fun getHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code in 500..599) {
                Timber.e("Got error code ${response.code}")
            }
            response
        }
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(interceptor)
        }
        return builder.build()
    }
}
