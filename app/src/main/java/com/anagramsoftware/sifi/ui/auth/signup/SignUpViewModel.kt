package com.anagramsoftware.sifi.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.anagramsoftware.sifi.data.source.Repository
import com.anagramsoftware.sifi.util.SingleLiveEvent
import com.google.firebase.auth.FirebaseUser
import io.reactivex.disposables.Disposable

class SignUpViewModel(private val repository: Repository): ViewModel() {

    val checkUserExistenceEvent = SingleLiveEvent<Boolean>()
    val checkUserFailedEvent = SingleLiveEvent<Void>()

    // Disposable
    private var disposable: Disposable? = null

    fun stop() {
        dispose()
    }

    fun checkUser(firebaseUser: FirebaseUser) {
        dispose()
        disposable = repository.checkUser(firebaseUser.uid)
                .subscribe({
                    checkUserExistenceEvent.value = it
                }, { checkUserFailedEvent.call() })
    }

    private fun dispose() {
        disposable?.let {
            if (!it.isDisposed)
                it.dispose()
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }

}