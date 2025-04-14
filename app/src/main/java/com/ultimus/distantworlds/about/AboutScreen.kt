/*
 *  Copyright 2025 Chris Margonis
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ultimus.distantworlds.about.AboutView.State
import com.ultimus.distantworlds.about.AboutView.UIAction
import com.ultimus.distantworlds.theme.DistantWorldsTheme
import com.ultimus.distantworlds_muzei.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen(state: State, onEvent: (UIAction) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = "Distant Worlds - Muzei", style = MaterialTheme.typography.titleLarge) }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.dw2logo_500),
                contentDescription = "App Logo",
            )
            Text(
                text = stringResource(R.string.app_name_formatted)
            )
            Text(
                text = stringResource(R.string.app_version),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.about_forum_thread),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.about_forum_thread_dw2),
                style = MaterialTheme.typography.bodyLarge
            )

            when (state) {
                State.Idle -> Unit
                State.InstallMuzeiPrompt -> {
                    Button(onClick = { onEvent(UIAction.InstallMuzeiClicked) }) {
                        Text(
                            text = stringResource(R.string.install_muzei)
                        )
                    }
                }

                is State.SelectDWSource -> {
                    if (state.showDW1 || state.showDW2) {
                        Button(onClick = { onEvent(UIAction.DW1Clicked) }) {
                            Text(
                                text = stringResource(R.string.enable_distant_worlds)
                            )
                        }
                        Button(onClick = { onEvent(UIAction.DW2Clicked) }) {
                            Text(
                                text = stringResource(R.string.enable_distant_worlds_2)
                            )
                        }
                    } else {
                        Button(onClick = { onEvent(UIAction.OpenMuzeiClicked) }) {
                            Text(
                                text = stringResource(R.string.open_muzei)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
internal fun AboutScreenPreview() {
    DistantWorldsTheme {
        Surface {
            AboutScreen(state = State.Idle, onEvent = {})
        }
    }
}
