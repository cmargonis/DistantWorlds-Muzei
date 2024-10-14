/*
 *  Copyright 2021 Chris Margonis
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

package com.ultimus.distantworlds.service

import androidx.work.Data
import com.ultimus.distantworlds.provider.DistantWorldsSource
import com.ultimus.distantworlds_muzei.BuildConfig

/**
 * @param source Whether images from Distant Worlds 1 or Distant Worlds 2 are selected.
 * @param albumId The Imgur album ID of the photo collection.
 */
data class WorkConfiguration(val source: DistantWorldsSource, val albumId: String, val authority: String) {

    companion object {

        /**
         * Constructs a configuration object based on which pictures will be fetched from remote source.
         */
        fun fromInput(inputData: Data): WorkConfiguration {
            val inputSource = inputData.getString(ImgurWorker.keySource)
                ?: throw IllegalArgumentException("Source not specified. Has to be one from ${DistantWorldsSource::name}")
            val source: DistantWorldsSource = DistantWorldsSource.valueOf(inputSource)
            val albumId = when (source) {
                DistantWorldsSource.DISTANT_WORLDS_1 -> BuildConfig.IMGUR_DW_ALBUM
                DistantWorldsSource.DISTANT_WORLDS_2 -> BuildConfig.IMGUR_DW2_ALBUM
            }

            val authority = when (source) {
                DistantWorldsSource.DISTANT_WORLDS_1 -> BuildConfig.DISTANT_WORLDS_AUTHORITY
                DistantWorldsSource.DISTANT_WORLDS_2 -> BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY
            }

            return WorkConfiguration(source = source, albumId = albumId, authority = authority)
        }
    }

}
