package com.ultimus.distantworlds.worker

import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.provider.DistantWorldsSource

class ImageProviderFactory(
    private val providers: Map<DistantWorldsSource, @JvmSuppressWildcards ImageProvider>,
) {

    fun getProvider(source: DistantWorldsSource): ImageProvider =
        providers[source] ?: throw IllegalArgumentException("No ImageProvider registered for source: $source")
}
