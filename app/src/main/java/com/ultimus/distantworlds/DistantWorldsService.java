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
