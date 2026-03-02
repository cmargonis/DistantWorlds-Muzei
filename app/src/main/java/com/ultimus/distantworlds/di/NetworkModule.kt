package com.ultimus.distantworlds.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ultimus.distantworlds.data.imgur.IMGUR_BASE_URL
import com.ultimus.distantworlds.data.imgur.ImgurService
import com.ultimus.distantworlds_muzei.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
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

    @Provides
    @Singleton
    fun provideImgurService(client: OkHttpClient): ImgurService {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return Retrofit.Builder()
            .baseUrl(IMGUR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ImgurService::class.java)
    }
}
