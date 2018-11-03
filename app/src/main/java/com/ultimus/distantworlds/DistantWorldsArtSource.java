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

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.ultimus.distantworlds.DistantWorldsService.ImageResponse;
import com.ultimus.distantworlds.model.Image;
import com.ultimus.distantworlds.util.NetworkUtils;
import com.ultimus.distantworlds.util.PrefUtils;
import com.ultimus.distantworlds_muzei.BuildConfig;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DistantWorldsArtSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "DistantWorldsArtSource";
    private static int ROTATE_TIME_MILLIS;
    private static final String TAG = "DWService";

    public DistantWorldsArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
        ROTATE_TIME_MILLIS = PrefUtils.getUpdateFrequencyInMillis(this);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        if (PrefUtils.getPrefOnlyWifi(this)) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Checking WiFi Connectivity");
            }
            // only on wifi - check if connected
            if (!NetworkUtils.isWifiConnected(this)) {
                // Not connected, schedule a new refresh and back away
                try {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "No Connectivity trying again later");
                    }
                    throw new RetryException();
                } catch (RetryException e) {
                    e.printStackTrace();
                }
                scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                return;
            }
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "WiFi available, proceeding");
            }
        }
        String currentToken = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : null;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        if (response.code() >= 500 && response.code() < 600) {
                            try {
                                throw new RetryException();
                            } catch (RetryException e) {
                                e.printStackTrace();
                            }
                            scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                        }
                        return response;
                    }
                });
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(interceptor);
        }
        OkHttpClient client = builder.build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DistantWorldsService.IMGUR_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(client)
                .build();

        DistantWorldsService service = retrofit.create(DistantWorldsService.class);
        Call<DistantWorldsService.AlbumResponse> response = service.getAlbumDetails(DistantWorldsService.ALBUM_ID, "Client-ID dc487820261fcea");
        retrofit2.Response<DistantWorldsService.AlbumResponse> album = null;
        try {
            album = response.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (album == null || !album.body().success) {
            throw new RetryException();
        }

        if (album.body().data == null) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "No photos returned from API.");
            }
            scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
            return;
        }

        Image photo;
        Random random = new Random();
        String token;
        List<Image> photosList = album.body().data.images;
        while (true) {
            photo = photosList.get(random.nextInt(photosList.size()));
            token = photo.id;
            if (photosList.size() <= 1 || !TextUtils.equals(token, currentToken)) {
                break;
            }
        }
        Call<ImageResponse> imageResponseCall = service.getSingleAlbumImage(DistantWorldsService.ALBUM_ID, photo.id, "Client-ID dc487820261fcea");
        try {
            retrofit2.Response<ImageResponse> img = imageResponseCall.execute();
            if (img != null && img.body() != null && img.body().success) {
                Image image = img.body().data;
                publishArtwork(new Artwork.Builder()
                        .title(image.title)
                        .byline(image.description)
                        .imageUri(Uri.parse(image.link))
                        .token(token)
                        .viewIntent(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(image.link)))
                        .build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }
}
