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
import com.ultimus.distantworlds.utils.CoroutinesTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class AboutViewModelTest {

    @JvmField
    @RegisterExtension
    val coroutinesTestExtension = CoroutinesTestExtension()

    private lateinit var testedClass: AboutViewModel

    @BeforeEach
    fun setup() {
        testedClass = AboutViewModel()
    }

    @Test
    fun `when select distant worlds 1 is pressed, then navigate to setup distant worlds 1`() = runBlockingTest {
        testedClass.effect.test {
            testedClass.onDistantWorlds1Clicked()
            assertEquals(AboutView.Navigation.ToDistantWorlds1, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when select distant worlds 2 is pressed, then navigate to setup distant worlds 2`() = runBlockingTest {
        testedClass.effect.test {
            testedClass.onDistantWorlds2Clicked()
            assertEquals(AboutView.Navigation.ToDistantWorlds2, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when install muzei is pressed, then navigate to play store to muzei page`() = runBlockingTest {
        testedClass.effect.test {
            testedClass.onInstallMuzeiClicked()
            assertEquals(AboutView.Navigation.ToInstallMuzei, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when open muzei is pressed, then navigate to open muzei page`() = runBlockingTest {
        testedClass.effect.test {
            testedClass.onOpenMuzeiClicked()
            assertEquals(AboutView.Navigation.ToOpenMuzei, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given muzei is not installed, when initializing, then emit install muzei state`() = runBlockingTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.NOT_INSTALLED)
        testedClass.state.test {
            assertEquals(AboutView.State.InstallMuzeiPrompt, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given distant worlds 1 & 2 is not selected, when initializing, then show select dw 1 & 2 button`() =
        runBlockingTest {
            testedClass.initialize(muzeiStatus = MuzeiStatus.SELECTED_NONE)

            testedClass.state.test {
                val expected = AboutView.State.SelectDWSource(showDW1 = true, showDW2 = true)
                assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given distant worlds 1 is selected, when initializing, then show select dw 2 button`() = runBlockingTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.DW_1_SELECTED)

        testedClass.state.test {
            val expected = AboutView.State.SelectDWSource(showDW1 = false, showDW2 = true)
            assertEquals(expected, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given distant worlds 2 is selected, when initializing, then show select dw 1 button`() = runBlockingTest {
        testedClass.initialize(muzeiStatus = MuzeiStatus.DW_2_SELECTED)

        testedClass.state.test {
            val expected = AboutView.State.SelectDWSource(showDW1 = true, showDW2 = false)
            assertEquals(expected, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
