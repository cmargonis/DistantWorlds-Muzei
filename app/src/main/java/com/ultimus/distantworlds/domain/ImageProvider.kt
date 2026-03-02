package com.ultimus.distantworlds.domain

import com.ultimus.distantworlds.domain.model.ArtworkData

fun interface ImageProvider {

    fun fetchArtwork(): List<ArtworkData>
}
