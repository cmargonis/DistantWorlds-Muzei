package com.ultimus.distantworlds.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import com.ultimus.distantworlds.provider.DistantWorldsSource
import com.ultimus.distantworlds.provider.authority
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.IOException

@HiltWorker
class ArtworkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val imageProviderFactory: ImageProviderFactory,
) : Worker(context, workerParams) {

    companion object {

        internal const val KEY_SOURCE: String = "artwork_source"

        fun enqueueLoad(source: DistantWorldsSource, context: Context?) {
            context ?: return
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder()
                .putAll(mutableMapOf<String, Any>(KEY_SOURCE to source.name))
                .build()
            workManager.enqueue(
                OneTimeWorkRequestBuilder<ArtworkWorker>().setInputData(data).build()
            )
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

        val providerClient = ProviderContract.getProviderClient(applicationContext, source.authority)
        providerClient.addArtwork(
            artworkList.map { artworkData ->
                Artwork(
                    token = artworkData.token,
                    title = artworkData.title,
                    byline = artworkData.byline,
                    webUri = artworkData.webUri.toUri(),
                    persistentUri = artworkData.imageUri.toUri(),
                )
            }.shuffled()
        )

        return Result.success()
    }
}
