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

package com.ultimus.distantworlds.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {

    val state: MutableStateFlow<AboutView.State> = MutableStateFlow(value = AboutView.State.Idle)
    val effect: MutableSharedFlow<AboutView.Navigation> = MutableSharedFlow(replay = 0)

    fun initialize(muzeiStatus: MuzeiStatus) {
        state.value = AboutView.State.InstallMuzeiPrompt
    }

    fun onDistantWorlds1Clicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToDistantWorlds1)
        }
    }

    fun onDistantWorlds2Clicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToDistantWorlds2)
        }
    }

    fun onInstallMuzeiClicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToInstallMuzei)
        }
    }

    fun onOpenMuzeiClicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToOpenMuzei)
        }
    }
}
