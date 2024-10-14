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
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.apps.muzei.api.MuzeiContract
import com.ultimus.distantworlds_muzei.BuildConfig
import com.ultimus.distantworlds_muzei.R
import com.ultimus.distantworlds_muzei.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AboutViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListeners()
        observeState()
    }

    private fun initializeListeners() {
        with(binding.layoutActions) {
            btnDistantWorlds1.setOnClickListener { viewModel.onDistantWorlds1Clicked() }
            btnDistantWorlds2.setOnClickListener { viewModel.onDistantWorlds2Clicked() }
            installMuzei.setOnClickListener { viewModel.onInstallMuzeiClicked() }
            openMuzei.setOnClickListener { viewModel.onOpenMuzeiClicked() }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect -> handle(effect) }
            }
        }
        viewModel.initialize(retrieveMuzeiStatus(requireContext()))
    }

    private fun retrieveMuzeiStatus(context: Context): MuzeiStatus {
        val distantWorldsSelected =
            MuzeiContract.Sources.isProviderSelected(requireContext(), BuildConfig.DISTANT_WORLDS_AUTHORITY)
        val distantWorlds2Selected =
            MuzeiContract.Sources.isProviderSelected(requireContext(), BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY)
        return when {
            !isMuzeiInstalled(context) -> MuzeiStatus.NOT_INSTALLED
            !distantWorldsSelected && !distantWorlds2Selected -> MuzeiStatus.SELECTED_NONE
            distantWorldsSelected -> MuzeiStatus.DW_1_SELECTED
            distantWorlds2Selected -> MuzeiStatus.DW_2_SELECTED
            else -> MuzeiStatus.NOT_INSTALLED
        }
    }

    private fun render(state: AboutView.State) {
        when (state) {
            AboutView.State.Idle -> {
            }
            AboutView.State.InstallMuzeiPrompt -> binding.layoutActions.installMuzei.isVisible = true
            is AboutView.State.SelectDWSource -> showSourceSelection(state)
        }
    }

    private fun showSourceSelection(state: AboutView.State.SelectDWSource) {
        with(binding.layoutActions) {
            if (state.showDW1 || state.showDW2) {
                btnDistantWorlds1.isVisible = true
                btnDistantWorlds2.isVisible = true
            } else {
                openMuzei.isVisible = true
            }
        }
    }

    private fun handle(effect: AboutView.Navigation) {
        when (effect) {
            AboutView.Navigation.ToDistantWorlds1 -> goToDistantWolds(
                BuildConfig.DISTANT_WORLDS_AUTHORITY,
                R.string.warning_select_source
            )
            AboutView.Navigation.ToDistantWorlds2 -> goToDistantWolds(
                BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY,
                R.string.warning_select_source_2
            )
            AboutView.Navigation.ToInstallMuzei -> goToInstallMuzei()
            AboutView.Navigation.ToOpenMuzei -> goToOpenMuzei()
        }
    }

    private fun goToDistantWolds(authority: String, @StringRes failedMessage: Int) {
        val deepLinkIntent = MuzeiContract.Sources.createChooseProviderIntent(authority)
        try {
            startActivity(deepLinkIntent)
        } catch (e: Exception) {
            Toast.makeText(activity, failedMessage, Toast.LENGTH_LONG).show()
            val muzeiLaunchIntent = activity?.packageManager?.getLaunchIntentForPackage(MainActivity.muzeiPackage)
            startActivity(muzeiLaunchIntent)
        }
    }

    private fun goToInstallMuzei() {
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

    private fun goToOpenMuzei() {
        val muzeiLaunchIntent = activity?.packageManager?.getLaunchIntentForPackage(MainActivity.muzeiPackage)
        activity?.startActivity(muzeiLaunchIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
