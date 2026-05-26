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

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ultimus.distantworlds.domain.ArtworkPublisher
import com.ultimus.distantworlds.domain.DistantWorldsSource
import timber.log.Timber
import java.io.IOException

class ArtworkWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val imageProviderFactory: ImageProviderFactory,
    private val publishers: Map<DistantWorldsSource, @JvmSuppressWildcards ArtworkPublisher>,
) : Worker(context, workerParams) {

    companion object {

        internal const val KEY_SOURCE: String = "artwork_source"

        fun enqueueLoad(source: DistantWorldsSource, context: Context?) {
            context ?: return
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder()
                .putAll(mutableMapOf<String, Any>(KEY_SOURCE to source.name))
                .build()
            workManager.enqueue(OneTimeWorkRequestBuilder<ArtworkWorker>().setInputData(data).build())
        }
    }

    override fun doWork(): Result {
        val sourceName = inputData.getString(KEY_SOURCE)
            ?: throw IllegalArgumentException("Source not specified. Has to be one from ${DistantWorldsSource::name}")
        val source = DistantWorldsSource.valueOf(sourceName)

        val provider = imageProviderFactory.getProvider(source)
        val artworkList = try {
            provider.fetchArtwork()
        } catch (e: IOException) {
            Timber.e(e)
            return Result.retry()
        }

        if (artworkList.isEmpty()) {
            Timber.w("No artwork returned for source: $source")
            return Result.failure()
        }

        val publisher = publishers[source]
            ?: throw IllegalArgumentException("No ArtworkPublisher registered for source: $source")
        publisher.publish(artworkList)

        return Result.success()
    }
}
