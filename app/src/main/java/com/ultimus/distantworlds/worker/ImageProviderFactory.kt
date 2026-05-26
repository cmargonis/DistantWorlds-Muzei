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

class ImageProviderFactory(
    private val providers: Map<DistantWorldsSource, @JvmSuppressWildcards ImageProvider>,
) {

    fun getProvider(source: DistantWorldsSource): ImageProvider =
        providers[source] ?: throw IllegalArgumentException("No ImageProvider registered for source: $source")
}
