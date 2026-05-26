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

package com.ultimus.distantworlds.worker

import com.ultimus.distantworlds.domain.DistantWorldsSource
import com.ultimus.distantworlds.domain.ImageProvider
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
