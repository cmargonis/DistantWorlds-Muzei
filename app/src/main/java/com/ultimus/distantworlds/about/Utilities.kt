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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.google.android.apps.muzei.api.MuzeiContract
import com.ultimus.distantworlds_muzei.BuildConfig
import com.ultimus.distantworlds_muzei.R

internal fun retrieveMuzeiStatus(context: Context): MuzeiStatus {
    val distantWorldsSelected =
        MuzeiContract.Sources.isProviderSelected(context, BuildConfig.DISTANT_WORLDS_AUTHORITY)
    val distantWorlds2Selected =
        MuzeiContract.Sources.isProviderSelected(context, BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
    return when {
        !isMuzeiInstalled(context) -> MuzeiStatus.NOT_INSTALLED
        !distantWorldsSelected && !distantWorlds2Selected -> MuzeiStatus.SELECTED_NONE
        distantWorldsSelected -> MuzeiStatus.DW_1_SELECTED
        distantWorlds2Selected -> MuzeiStatus.DW_2_SELECTED
        else -> MuzeiStatus.NOT_INSTALLED
    }
}

private fun isMuzeiInstalled(context: Context): Boolean {
    val packageManager = context.packageManager
    return try {
        val info = packageManager.getApplicationInfo(MainActivity.MUZEI_PACKAGE, 0)
        info.enabled
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}

internal fun goToOpenMuzei(context: Context) {
    val muzeiLaunchIntent = context.packageManager.getLaunchIntentForPackage(MainActivity.MUZEI_PACKAGE)
    muzeiLaunchIntent?.let { context.startActivity(it) }
}

internal fun goToDistantWolds(context: Context, authority: String, @StringRes failedMessage: Int) {
    val deepLinkIntent = MuzeiContract.Sources.createChooseProviderIntent(authority)
    try {
        context.startActivity(deepLinkIntent)
    } catch (_: Exception) {
        Toast.makeText(context, failedMessage, Toast.LENGTH_LONG).show()
        val muzeiLaunchIntent = context.packageManager.getLaunchIntentForPackage(MainActivity.MUZEI_PACKAGE)
        muzeiLaunchIntent?.let { context.startActivity(it) }
    }
}

internal fun goToInstallMuzei(context: Context) {
    val installIntent = Intent(
        Intent.ACTION_VIEW,
        "https://play.google.com/store/apps/details?id=${MainActivity.MUZEI_PACKAGE}".toUri()
    )
    try {
        context.startActivity(installIntent)
    } catch (_: Exception) {
        Toast.makeText(context, R.string.warning_muzei_not_installed, Toast.LENGTH_LONG).show()
    }
}
