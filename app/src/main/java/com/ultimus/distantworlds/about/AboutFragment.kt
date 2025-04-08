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

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ultimus.distantworlds.about.AboutView.Navigation
import com.ultimus.distantworlds.about.AboutView.State
import com.ultimus.distantworlds.about.AboutView.UIAction
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListeners()
        observeState()
    }

    private fun initializeListeners() {
        with(binding.layoutActions) {
            btnDistantWorlds1.setOnClickListener { viewModel.onUserAction(UIAction.DW1Clicked) }
            btnDistantWorlds2.setOnClickListener { viewModel.onUserAction(UIAction.DW2Clicked) }
            installMuzei.setOnClickListener { viewModel.onUserAction(UIAction.InstallMuzeiClicked) }
            openMuzei.setOnClickListener { viewModel.onUserAction(UIAction.OpenMuzeiClicked) }
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

    private fun render(state: State) {
        when (state) {
            State.Idle -> Unit
            State.InstallMuzeiPrompt -> binding.layoutActions.installMuzei.isVisible = true
            is State.SelectDWSource -> showSourceSelection(state)
        }
    }

    private fun showSourceSelection(state: State.SelectDWSource) {
        with(binding.layoutActions) {
            if (state.showDW1 || state.showDW2) {
                btnDistantWorlds1.isVisible = true
                btnDistantWorlds2.isVisible = true
            } else {
                openMuzei.isVisible = true
            }
        }
    }

    private fun handle(effect: Navigation) {
        when (effect) {
            Navigation.ToDistantWorlds1 -> goToDistantWolds(
                requireContext(),
                BuildConfig.DISTANT_WORLDS_AUTHORITY,
                R.string.warning_select_source
            )

            Navigation.ToDistantWorlds2 -> goToDistantWolds(
                requireContext(),
                BuildConfig.DISTANT_WORLDS_TWO_AUTHORITY,
                R.string.warning_select_source_2
            )

            Navigation.ToInstallMuzei -> goToInstallMuzei(requireContext())
            Navigation.ToOpenMuzei -> goToOpenMuzei(requireContext())
        }
    }
}
