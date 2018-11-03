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

package com.ultimus.distantworlds;

import com.google.gson.annotations.SerializedName;
import com.ultimus.distantworlds.model.Album;
import com.ultimus.distantworlds.model.Image;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by cmargonis on 28/3/2016.
 */
interface DistantWorldsService {
    String ALBUM_ID = "Upm7x";
    String IMGUR_API_VERSION = "3";
    String IMGUR_BASE_URL = "https://api.imgur.com/" + IMGUR_API_VERSION + "/";

    @GET("image")
    Call<PhotosResponse> getPopularPhotos(@Query("max_results") Integer maxResults);

    @GET("album/{id}")
    Call<AlbumResponse> getAlbumDetails(@Path("id") String albumId, @Header("Authorization") String clientId);

    @GET("album/{id}/image/{imageId}")
    Call<ImageResponse> getSingleAlbumImage(@Path("id") String albumId, @Path("imageId") String imageId, @Header("Authorization") String clientId);

    @GET("album/{id}/images")
    Call<Object> getAlbumImages(@Path("id") String albumId);


    class AlbumResponse {
        Album data;
        boolean success;
        int status;
    }

    class ImageResponse {
        Image data;
        boolean success;
        int status;
    }


    class PhotosResponse {
        @SerializedName("resources")
        List<Photo> photos;
        String nextCursor;
    }

    class Photo {
        String publicId;
        int width;
        int height;
        String url;
        String secureUrl;
    }
}
