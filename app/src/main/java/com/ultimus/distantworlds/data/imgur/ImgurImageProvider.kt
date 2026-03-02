package com.ultimus.distantworlds.data.imgur

import com.ultimus.distantworlds.domain.ImageProvider
import com.ultimus.distantworlds.domain.model.ArtworkData
import timber.log.Timber

class ImgurImageProvider(
    private val service: ImgurService,
    private val albumId: String,
    private val clientId: String,
) : ImageProvider {

    override fun fetchArtwork(): List<ArtworkData> {
        val response = service.getAlbumDetails(albumId, clientId).execute()

        val body = response.body()
        if (body?.success == false) {
            return emptyList()
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
