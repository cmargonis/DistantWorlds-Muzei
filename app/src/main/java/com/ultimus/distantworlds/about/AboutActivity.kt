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
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ultimus.distantworlds.R

class AboutActivity : AppCompatActivity() {

    private lateinit var warning: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        warning = findViewById(R.id.muzeiNotInstalledLabel)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        determineWarningVisibility()
    }

    private fun determineWarningVisibility() {
        val muzeiInstalled = isMuzeiInstalled(this)
        warning.visibility = if (muzeiInstalled) View.GONE else View.VISIBLE
    }

    private fun isMuzeiInstalled(context: Context): Boolean {
        val muzeiPackage = "net.nurik.roman.muzei"
        val packageManager = context.packageManager
        return try {
            val info = packageManager.getApplicationInfo(muzeiPackage, 0)
            info.enabled
        } catch (ex: PackageManager.NameNotFoundException) {
            false
        }
    }
}
