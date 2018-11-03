/*
 *  Copyright 2018 Chris Margonis
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

package com.ultimus.distantworlds

import com.ultimus.distantworlds.model.AlbumResponse
import com.ultimus.distantworlds.model.ImageResponse
import com.ultimus.distantworlds.model.PhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by cmargonis on 28/3/2016.
 */
const val ALBUM_ID = "Upm7x"
const val IMGUR_API_VERSION = "3"
const val IMGUR_BASE_URL = "https://api.imgur.com/$IMGUR_API_VERSION/"

internal interface DistantWorldsService {
    @GET("image")
    fun getPopularPhotos(@Query("max_results") maxResults: Int?): Call<PhotosResponse>

    @GET("album/{id}")
    fun getAlbumDetails(@Path("id") albumId: String, @Header("Authorization") clientId: String): Call<AlbumResponse>

    @GET("album/{id}/image/{imageId}")
    fun getSingleAlbumImage(@Path("id") albumId: String, @Path("imageId") imageId: String, @Header("Authorization") clientId: String): Call<ImageResponse>

    @GET("album/{id}/images")
    fun getAlbumImages(@Path("id") albumId: String): Call<Any>
}
