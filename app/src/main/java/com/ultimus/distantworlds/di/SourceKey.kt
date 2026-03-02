package com.ultimus.distantworlds.di

import com.ultimus.distantworlds.provider.DistantWorldsSource
import dagger.MapKey

@MapKey
annotation class SourceKey(val value: DistantWorldsSource)
