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
            assertEquals(AboutView.Navigation.ToDistantWorlds1, this.awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when select distant worlds 2 is pressed, then navigate to setup distant worlds 2`() = runBlockingTest {
        testedClass.effect.test {
            testedClass.onDistantWorlds2Clicked()
            assertEquals(AboutView.Navigation.ToDistantWorlds2, this.awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
