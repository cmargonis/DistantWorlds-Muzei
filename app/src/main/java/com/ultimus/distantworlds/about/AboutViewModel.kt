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
import com.ultimus.distantworlds.about.AboutView.State
import com.ultimus.distantworlds.about.AboutView.UIAction.DW1Clicked
import com.ultimus.distantworlds.about.AboutView.UIAction.DW2Clicked
import com.ultimus.distantworlds.about.AboutView.UIAction.InstallMuzeiClicked
import com.ultimus.distantworlds.about.AboutView.UIAction.OpenMuzeiClicked
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {

    val state: MutableStateFlow<State> = MutableStateFlow(value = State.Idle)
    val effect: MutableSharedFlow<AboutView.Navigation> = MutableSharedFlow(replay = 0)

    fun initialize(muzeiStatus: MuzeiStatus) {
        state.value = when (muzeiStatus) {
            MuzeiStatus.NOT_INSTALLED -> State.InstallMuzeiPrompt
            MuzeiStatus.SELECTED_NONE -> State.SelectDWSource(showDW1 = true, showDW2 = true)
            MuzeiStatus.DW_1_SELECTED -> State.SelectDWSource(showDW1 = false, showDW2 = true)
            MuzeiStatus.DW_2_SELECTED -> State.SelectDWSource(showDW1 = true, showDW2 = false)
        }
    }

    fun onUserAction(action: AboutView.UIAction) {
        when (action) {
            is DW1Clicked -> onDistantWorlds1Clicked()
            is DW2Clicked -> onDistantWorlds2Clicked()
            is InstallMuzeiClicked -> onInstallMuzeiClicked()
            is OpenMuzeiClicked -> onOpenMuzeiClicked()
        }
    }

    private fun onDistantWorlds1Clicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToDistantWorlds1)
        }
    }

    private fun onDistantWorlds2Clicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToDistantWorlds2)
        }
    }

    private fun onInstallMuzeiClicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToInstallMuzei)
        }
    }

    private fun onOpenMuzeiClicked() {
        viewModelScope.launch {
            effect.emit(AboutView.Navigation.ToOpenMuzei)
        }
    }
}
