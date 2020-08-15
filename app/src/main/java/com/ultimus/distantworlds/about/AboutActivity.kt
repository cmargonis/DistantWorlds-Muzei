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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.apps.muzei.api.MuzeiContract
import com.google.android.apps.muzei.api.MuzeiContract.Sources.createChooseProviderIntent
import com.ultimus.distantworlds.BuildConfig
import com.ultimus.distantworlds.R

class AboutActivity : AppCompatActivity() {
    companion object {
        const val muzeiPackage = "net.nurik.roman.muzei"
    }

    private lateinit var redirectAnimator: ViewAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        redirectAnimator = findViewById(R.id.redirect_animator)
        setSupportActionBar(toolbar)
        val muzeiLaunchIntent = packageManager.getLaunchIntentForPackage(muzeiPackage)
        findViewById<Button>(R.id.enable1).setOnClickListener {
            val deepLinkIntent = createChooseProviderIntent(
                    BuildConfig.DISTANT_WORLDS_AUTHORITY)
            try {
                startActivity(deepLinkIntent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.warning_select_source,
                        Toast.LENGTH_LONG).show()
                startActivity(muzeiLaunchIntent)
            }
        }
        findViewById<Button>(R.id.enable2).setOnClickListener {
            val deepLinkIntent = createChooseProviderIntent(
                    BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
            try {
                startActivity(deepLinkIntent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.warning_select_source_2,
                        Toast.LENGTH_LONG).show()
                startActivity(muzeiLaunchIntent)
            }
        }
        findViewById<Button>(R.id.install_muzei).setOnClickListener {
            val installIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$muzeiPackage"))
            try {
                startActivity(installIntent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.warning_muzei_not_installed,
                        Toast.LENGTH_LONG).show()
            }
        }
        findViewById<Button>(R.id.open_muzei).setOnClickListener {
            startActivity(muzeiLaunchIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        determineRedirectVisibility()
    }

    private fun determineRedirectVisibility() {
        val distantWorldsSelected = MuzeiContract.Sources.isProviderSelected(
                this, BuildConfig.DISTANT_WORLDS_AUTHORITY)
        val distantWorlds2Selected = MuzeiContract.Sources.isProviderSelected(
                this, BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
        if (distantWorldsSelected || distantWorlds2Selected) {
            redirectAnimator.displayedChild = 2
        } else {
            val muzeiInstalled = isMuzeiInstalled(this)
            redirectAnimator.displayedChild = if (muzeiInstalled) 0 else 1
        }
    }

    private fun isMuzeiInstalled(context: Context): Boolean {
        val packageManager = context.packageManager
        return try {
            val info = packageManager.getApplicationInfo(muzeiPackage, 0)
            info.enabled
        } catch (ex: PackageManager.NameNotFoundException) {
            false
        }
    }
}
