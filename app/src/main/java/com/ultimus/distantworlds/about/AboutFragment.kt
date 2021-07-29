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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.apps.muzei.api.MuzeiContract
import com.ultimus.distantworlds.BuildConfig
import com.ultimus.distantworlds.R
import com.ultimus.distantworlds.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val muzeiLaunchIntent = activity?.packageManager?.getLaunchIntentForPackage(MainActivity.muzeiPackage)
        binding.redirectAnimator.enable1.setOnClickListener {
            val deepLinkIntent = MuzeiContract.Sources.createChooseProviderIntent(BuildConfig.DISTANT_WORLDS_AUTHORITY)
            try {
                startActivity(deepLinkIntent)
            } catch (e: Exception) {
                Toast.makeText(activity, R.string.warning_select_source, Toast.LENGTH_LONG).show()
                startActivity(muzeiLaunchIntent)
            }
        }
        binding.redirectAnimator.enable2.setOnClickListener {
            val deepLinkIntent = MuzeiContract.Sources.createChooseProviderIntent(BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
            try {
                startActivity(deepLinkIntent)
            } catch (e: Exception) {
                Toast.makeText(activity, R.string.warning_select_source_2, Toast.LENGTH_LONG).show()
                startActivity(muzeiLaunchIntent)
            }
        }
        binding.redirectAnimator.installMuzei.setOnClickListener {
            val installIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${MainActivity.muzeiPackage}")
            )
            try {
                startActivity(installIntent)
            } catch (e: Exception) {
                Toast.makeText(activity, R.string.warning_muzei_not_installed, Toast.LENGTH_LONG).show()
            }
        }
        binding.redirectAnimator.openMuzei.setOnClickListener { startActivity(muzeiLaunchIntent) }

        determineRedirectVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun determineRedirectVisibility() {
        val distantWorldsSelected =
            MuzeiContract.Sources.isProviderSelected(requireContext(), BuildConfig.DISTANT_WORLDS_AUTHORITY)
        val distantWorlds2Selected =
            MuzeiContract.Sources.isProviderSelected(requireContext(), BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
        if (distantWorldsSelected || distantWorlds2Selected) {
            binding.redirectAnimator.redirectAnimator.displayedChild = 2
        } else {
            val muzeiInstalled = isMuzeiInstalled(requireContext())
            binding.redirectAnimator.redirectAnimator.displayedChild = if (muzeiInstalled) 0 else 1
        }
    }

    private fun isMuzeiInstalled(context: Context): Boolean {
        val packageManager = context.packageManager
        return try {
            val info = packageManager.getApplicationInfo(MainActivity.muzeiPackage, 0)
            info.enabled
        } catch (ex: PackageManager.NameNotFoundException) {
            false
        }
    }
}