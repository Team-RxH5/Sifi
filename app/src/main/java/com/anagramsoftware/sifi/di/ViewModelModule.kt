package com.anagramsoftware.sifi.di

import com.anagramsoftware.sifi.ui.auth.createuser.CreateUserViewModel
import com.anagramsoftware.sifi.ui.auth.signup.SignUpViewModel
import com.anagramsoftware.sifi.ui.profile.ProfileViewModel
import com.anagramsoftware.sifi.ui.provide.ProvideViewModel
import com.anagramsoftware.sifi.ui.selectnetwork.SelectNetworkViewModel
import com.anagramsoftware.sifi.ui.use.UseViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { UseViewModel(get()) }
    viewModel { ProvideViewModel() }
    viewModel { ProfileViewModel(get()) }
    viewModel { SelectNetworkViewModel() }
    viewModel { SignUpViewModel(get()) }
    viewModel { CreateUserViewModel(get()) }
}