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

package com.ultimus.distantworlds.data.imgur

import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.domain.model.ArtworkData
import timber.log.Timber
import java.io.IOException

class ImgurImageProvider(
    private val service: ImgurService,
    private val albumId: String,
    private val clientId: String,
) : ImageProvider {

    override fun fetchArtwork(): List<ArtworkData> {
        val response = service.getAlbumDetails(albumId, clientId).execute()

        val body = response.body()
        if (body?.success == false) {
            throw IOException("Imgur API returned success=false for album $albumId")
        }

        val images = body?.data?.images
        if (images.isNullOrEmpty()) {
            Timber.w("No photos returned from API.")
            return emptyList()
        }

        return images.map { image ->
            ArtworkData(
                token = image.id,
                title = image.title,
                byline = image.description,
                imageUri = image.link,
                webUri = image.link,
            )
        }
    }
}
