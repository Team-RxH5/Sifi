package com.anagramsoftware.sifi.di

import com.anagramsoftware.sifi.ui.profile.ProfileViewModel
import com.anagramsoftware.sifi.ui.provide.ProvideViewModel
import com.anagramsoftware.sifi.ui.selectnetwork.SelectNetworkViewModel
import com.anagramsoftware.sifi.ui.use.UseViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val viewModelModule = applicationContext {
    viewModel { UseViewModel(get()) }
    viewModel { ProvideViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { SelectNetworkViewModel() }
}