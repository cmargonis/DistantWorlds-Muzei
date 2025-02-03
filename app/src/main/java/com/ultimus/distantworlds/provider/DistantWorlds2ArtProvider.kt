/*
 *  Copyright 2019 Chris Margonis
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

package com.ultimus.distantworlds.provider

import com.google.android.apps.muzei.api.provider.MuzeiArtDocumentsProvider
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import com.ultimus.distantworlds.service.ImgurWorker

/**
 * Created by Chris Margonis on 03/11/2018.
 */
class DistantWorlds2ArtProvider : MuzeiArtProvider() {

    override fun onLoadRequested(initial: Boolean) {
        ImgurWorker.enqueueLoad(DistantWorldsSource.DISTANT_WORLDS_2, context)
    }
}

class DistantWorlds2ArtDocumentsProvider : MuzeiArtDocumentsProvider()
