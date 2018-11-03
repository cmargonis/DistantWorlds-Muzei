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

package com.ultimus.distantworlds

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.google.android.apps.muzei.api.Artwork
import com.google.android.apps.muzei.api.MuzeiArtSource
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ultimus.distantworlds.model.AlbumResponse
import com.ultimus.distantworlds.model.Image
import com.ultimus.distantworlds.util.PrefUtils
import com.ultimus.distantworlds.util.isWifiConnected
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * TODO: Customize class - update intent actions and extra parameters.
 */
private const val SOURCE_NAME = "DistantWorldsArtSource"

class DistantWorldsArtSource : RemoteMuzeiArtSource(SOURCE_NAME) {
    private val tag = "DWService"
    private var rotateTimeMillis: Int = 0

    override fun onCreate() {
        super.onCreate()
        setUserCommands(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK)
        rotateTimeMillis = PrefUtils.getUpdateFrequencyInMillis(this)
    }

    @Throws(RemoteMuzeiArtSource.RetryException::class)
    override fun onTryUpdate(reason: Int) {
        if (PrefUtils.getPrefOnlyWifi(this)) {
            if (BuildConfig.DEBUG) {
                Log.i(tag, "Checking WiFi Connectivity")
            }
            // only on wifi - check if connected
            if (!isWifiConnected(this)) {
                // Not connected, schedule a new refresh and back away
                try {
                    if (BuildConfig.DEBUG) {
                        Log.w(tag, "No Connectivity trying again later")
                    }
                    throw RemoteMuzeiArtSource.RetryException()
                } catch (e: RemoteMuzeiArtSource.RetryException) {
                    e.printStackTrace()
                }

                scheduleUpdate(System.currentTimeMillis() + rotateTimeMillis)
                return
            }
            if (BuildConfig.DEBUG) {
                Log.i(tag, "WiFi available, proceeding")
            }
        }
        val currentToken = if (currentArtwork != null) currentArtwork.token else null

        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code() >= 500 && response.code() < 600) {
                try {
                    throw RemoteMuzeiArtSource.RetryException()
                } catch (e: RemoteMuzeiArtSource.RetryException) {
                    e.printStackTrace()
                }

                scheduleUpdate(System.currentTimeMillis() + rotateTimeMillis)
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
        val response = service.getAlbumDetails(ALBUM_ID, "Client-ID dc487820261fcea")
        var album: retrofit2.Response<AlbumResponse>? = null
        try {
            album = response.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (album == null || !album.body().success) {
            throw RemoteMuzeiArtSource.RetryException()
        }

        if (album.body().data == null) {
            if (BuildConfig.DEBUG) {
                Log.w(tag, "No photos returned from API.")
            }
            scheduleUpdate(System.currentTimeMillis() + rotateTimeMillis)
            return
        }

        var photo: Image
        val random = Random()
        var token: String
        val photosList = album.body().data?.images
        while (true) {
            photo = photosList!![random.nextInt(photosList.size)]
            token = photo.id
            if (photosList.size <= 1 || !TextUtils.equals(token, currentToken)) {
                break
            }
        }
        val imageResponseCall = service.getSingleAlbumImage(ALBUM_ID, photo.id, "Client-ID dc487820261fcea")
        try {
            val img = imageResponseCall.execute()
            if (img?.body() != null && img.body().success) {
                val image = img.body().data
                publishArtwork(
                    Artwork.Builder()
                        .title(image.title)
                        .byline(image.description)
                        .imageUri(Uri.parse(image.link))
                        .token(token)
                        .viewIntent(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(image.link)
                            )
                        )
                        .build()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        scheduleUpdate(System.currentTimeMillis() + rotateTimeMillis)
    }

}

