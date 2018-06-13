package com.anagramsoftware.sifi.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anagramsoftware.sifi.data.model.User
import com.anagramsoftware.sifi.data.source.Repository
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    val user = MutableLiveData<User>()

    // Disposables
    val compositeDisposable = CompositeDisposable()

    fun start() {
        getUser()
    }

    private fun getUser() {
        compositeDisposable.add(repository.getCurrentUser()
                .subscribe{
                    user.value = it
                })
    }

}
