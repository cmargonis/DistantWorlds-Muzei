package com.ultimus.distantworlds.di

import com.ultimus.distantworlds.data.imgur.ImgurImageProvider
import com.ultimus.distantworlds.data.imgur.ImgurService
import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.provider.DistantWorldsSource
import com.ultimus.distantworlds_muzei.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
object ImageProviderModule {

    @Provides
    @IntoMap
    @SourceKey(DistantWorldsSource.DISTANT_WORLDS_1)
    fun provideDistantWorlds1Provider(service: ImgurService): ImageProvider =
        ImgurImageProvider(service, BuildConfig.IMGUR_DW_ALBUM, BuildConfig.IMGUR_CLIENT_ID)

    @Provides
    @IntoMap
    @SourceKey(DistantWorldsSource.DISTANT_WORLDS_2)
    fun provideDistantWorlds2Provider(service: ImgurService): ImageProvider =
        ImgurImageProvider(service, BuildConfig.IMGUR_DW2_ALBUM, BuildConfig.IMGUR_CLIENT_ID)
}
