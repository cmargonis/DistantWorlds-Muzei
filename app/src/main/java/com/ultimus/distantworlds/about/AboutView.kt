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

object AboutView {

    sealed class State {
        object Idle : State()
        object InstallMuzeiPrompt : State()
        data class SelectDWSource(val showDW1: Boolean, val showDW2: Boolean) : State()
    }

    sealed class Navigation {
        object ToDistantWorlds1 : Navigation()
        object ToDistantWorlds2 : Navigation()
        object ToInstallMuzei : Navigation()
        object ToOpenMuzei : Navigation()
    }

    sealed interface UIAction {
        data object DW1Clicked : UIAction
        data object DW2Clicked : UIAction
        data object InstallMuzeiClicked : UIAction
        data object OpenMuzeiClicked : UIAction
    }
}

enum class MuzeiStatus {
    NOT_INSTALLED,
    SELECTED_NONE,
    DW_1_SELECTED,
    DW_2_SELECTED
}
