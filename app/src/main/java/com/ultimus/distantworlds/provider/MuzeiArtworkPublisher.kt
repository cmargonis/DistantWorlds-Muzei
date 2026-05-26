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

package com.ultimus.distantworlds.provider

import android.content.Context
import androidx.core.net.toUri
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import com.ultimus.distantworlds.domain.ArtworkPublisher
import com.ultimus.distantworlds.domain.model.ArtworkData

class MuzeiArtworkPublisher(
    private val context: Context,
    private val authority: String,
) : ArtworkPublisher {

    override fun publish(artworkList: List<ArtworkData>) {
        val providerClient = ProviderContract.getProviderClient(context, authority)
        providerClient.addArtwork(
            artworkList.map { artwork ->
                Artwork(
                    token = artwork.token,
                    title = artwork.title,
                    byline = artwork.byline,
                    webUri = artwork.webUri.toUri(),
                    persistentUri = artwork.imageUri.toUri(),
                )
            }.shuffled()
        )
    }
}
