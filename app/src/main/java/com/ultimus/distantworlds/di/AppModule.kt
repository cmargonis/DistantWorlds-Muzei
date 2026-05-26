/*
 *  Copyright 2026 Chris Margonis
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

package com.ultimus.distantworlds.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ultimus.distantworlds.about.AboutViewModel
import com.ultimus.distantworlds.data.imgur.IMGUR_BASE_URL
import com.ultimus.distantworlds.data.imgur.ImgurImageProvider
import com.ultimus.distantworlds.data.imgur.ImgurService
import com.ultimus.distantworlds.domain.ArtworkPublisher
import com.ultimus.distantworlds.domain.DistantWorldsSource
import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.provider.MuzeiArtworkPublisher
import com.ultimus.distantworlds.worker.ArtworkWorker
import com.ultimus.distantworlds.worker.ImageProviderFactory
import com.ultimus.distantworlds_muzei.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val appModule: Module = module {
    single<OkHttpClient> {
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
        builder.build()
    }

    single<ImgurService> {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        Retrofit.Builder()
            .baseUrl(IMGUR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(get())
            .build()
            .create(ImgurService::class.java)
    }

    single<Map<DistantWorldsSource, ImageProvider>> {
        mapOf(
            DistantWorldsSource.DISTANT_WORLDS_1 to ImgurImageProvider(
                get(),
                BuildConfig.IMGUR_DW_ALBUM,
                BuildConfig.IMGUR_CLIENT_ID,
            ),
            DistantWorldsSource.DISTANT_WORLDS_2 to ImgurImageProvider(
                get(),
                BuildConfig.IMGUR_DW2_ALBUM,
                BuildConfig.IMGUR_CLIENT_ID,
            ),
        )
    }

    single { ImageProviderFactory(get()) }

    single<Map<DistantWorldsSource, @JvmSuppressWildcards ArtworkPublisher>> {
        val context = get<android.content.Context>()
        mapOf(
            DistantWorldsSource.DISTANT_WORLDS_1 to MuzeiArtworkPublisher(
                context,
                BuildConfig.DISTANT_WORLDS_AUTHORITY,
            ),
            DistantWorldsSource.DISTANT_WORLDS_2 to MuzeiArtworkPublisher(
                context,
                BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY,
            ),
        )
    }

    worker { ArtworkWorker(get(), get(), get(), get()) }

    viewModel { AboutViewModel() }
}
