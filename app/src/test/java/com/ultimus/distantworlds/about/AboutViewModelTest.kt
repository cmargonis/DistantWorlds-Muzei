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

import app.cash.turbine.test
import com.ultimus.distantworlds.about.AboutView.UIAction
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AboutViewModelTest {

    private lateinit var testedClass: AboutViewModel

    @BeforeEach
    fun setup() {
        testedClass = AboutViewModel()
    }

    @Test
    fun `when select distant worlds 1 is pressed, then navigate to setup distant worlds 1`() = runTest {
        testedClass.effect.test {
            testedClass.onUserAction(UIAction.DW1Clicked)
            assertEquals(AboutView.Navigation.ToDistantWorlds1, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when select distant worlds 2 is pressed, then navigate to setup distant worlds 2`() = runTest {
        testedClass.effect.test {
            testedClass.onUserAction(UIAction.DW2Clicked)
            assertEquals(AboutView.Navigation.ToDistantWorlds2, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when install muzei is pressed, then navigate to play store to muzei page`() = runTest {
        testedClass.effect.test {
            testedClass.onUserAction(UIAction.InstallMuzeiClicked)
            assertEquals(AboutView.Navigation.ToInstallMuzei, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when open muzei is pressed, then navigate to open muzei page`() = runTest {
        testedClass.effect.test {
            testedClass.onUserAction(UIAction.OpenMuzeiClicked)
            assertEquals(AboutView.Navigation.ToOpenMuzei, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given muzei is not installed, when initializing, then emit install muzei state`() = runTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.NOT_INSTALLED)
        testedClass.state.test {
            assertEquals(AboutView.State.InstallMuzeiPrompt, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given distant worlds 1 & 2 is not selected, when initializing, then open muzei button`() = runTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.SELECTED_NONE)

        testedClass.state.test {
            val expected = AboutView.State.SelectDWSource(showDW1 = false, showDW2 = false)
            assertEquals(expected, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given distant worlds 1 is selected, when initializing, then show select dw 2 button`() = runTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.DW_1_SELECTED)

        testedClass.state.test {
            val expected = AboutView.State.SelectDWSource(showDW1 = false, showDW2 = true)
            assertEquals(expected, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given distant worlds 2 is selected, when initializing, then show select dw 1 button`() = runTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.DW_2_SELECTED)

        testedClass.state.test {
            val expected = AboutView.State.SelectDWSource(showDW1 = true, showDW2 = false)
            assertEquals(expected, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
