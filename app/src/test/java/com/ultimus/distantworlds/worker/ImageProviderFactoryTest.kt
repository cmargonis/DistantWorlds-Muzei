package com.ultimus.distantworlds.worker

import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.provider.DistantWorldsSource
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ImageProviderFactoryTest {

    @Test
    fun `given provider registered for DW1, when getting provider, then return correct provider`() {
        val dw1Provider: ImageProvider = mockk()
        val factory = ImageProviderFactory(mapOf(DistantWorldsSource.DISTANT_WORLDS_1 to dw1Provider))

        val result = factory.getProvider(DistantWorldsSource.DISTANT_WORLDS_1)

        assertSame(dw1Provider, result)
    }

    @Test
    fun `given provider registered for DW2, when getting provider, then return correct provider`() {
        val dw2Provider: ImageProvider = mockk()
        val factory = ImageProviderFactory(mapOf(DistantWorldsSource.DISTANT_WORLDS_2 to dw2Provider))

        val result = factory.getProvider(DistantWorldsSource.DISTANT_WORLDS_2)

        assertSame(dw2Provider, result)
    }

    @Test
    fun `given no provider registered, when getting provider, then throw IllegalArgumentException`() {
        val factory = ImageProviderFactory(emptyMap())

        assertThrows(IllegalArgumentException::class.java) {
            factory.getProvider(DistantWorldsSource.DISTANT_WORLDS_1)
        }
    }
}
