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

package com.ultimus.distantworlds.about

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ultimus.distantworlds.about.AboutView.Navigation
import com.ultimus.distantworlds.theme.DistantWorldsTheme
import com.ultimus.distantworlds_muzei.BuildConfig
import com.ultimus.distantworlds_muzei.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {

        const val MUZEI_PACKAGE: String = "net.nurik.roman.muzei"
    }

    private val viewModel: AboutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect -> handle(effect) }
            }
        }
        viewModel.initialize(retrieveMuzeiStatus(this))
        setContent {
            DistantWorldsTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                AboutScreen(
                    state = state,
                    onEvent = viewModel::onUserAction
                )
            }
        }
    }

    private fun handle(effect: Navigation) {
        when (effect) {
            Navigation.ToDistantWorlds1 -> goToDistantWolds(
                this,
                BuildConfig.DISTANT_WORLDS_AUTHORITY,
                R.string.warning_select_source
            )

            Navigation.ToDistantWorlds2 -> goToDistantWolds(
                this,
                BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY,
                R.string.warning_select_source_2
            )

            Navigation.ToInstallMuzei -> goToInstallMuzei(this)
            Navigation.ToOpenMuzei -> goToOpenMuzei(this)
        }
    }
}
