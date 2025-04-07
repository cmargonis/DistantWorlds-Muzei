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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ultimus.distantworlds.theme.DistantWorldsTheme
import com.ultimus.distantworlds_muzei.R

@Composable
internal fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
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
            text = stringResource(R.string.app_version)
        )
        Text(
            text = stringResource(R.string.about_description)
        )
        Text(
            text = stringResource(R.string.about_forum_thread),
        )
        Text(
            text = stringResource(R.string.about_forum_thread_dw2),
        )

        Button(onClick = {}) {
            Text(
                text = stringResource(R.string.enable_distant_worlds)
            )
        }
        Button(onClick = {}) {
            Text(
                text = stringResource(R.string.enable_distant_worlds_2)
            )
        }

        Button(onClick = {}) {
            Text(
                text = stringResource(R.string.install_muzei)
            )
        }

        Button(onClick = {}) {
            Text(
                text = stringResource(R.string.open_muzei)
            )
        }
    }
}

@Preview
@Composable
internal fun AboutScreenPreview() {
    DistantWorldsTheme {
        Surface {
            AboutScreen()
        }
    }
}
