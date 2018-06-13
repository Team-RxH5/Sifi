package com.anagramsoftware.sifi.ui.auth.createuser

import android.util.Log
import androidx.lifecycle.ViewModel
import com.anagramsoftware.sifi.data.model.User
import com.anagramsoftware.sifi.data.source.Repository
import com.anagramsoftware.sifi.util.SingleLiveEvent
import io.reactivex.disposables.Disposable

class CreateUserViewModel(private val repository: Repository) : ViewModel() {

    val createUserCompleteEvent = SingleLiveEvent<Boolean>()

    // Disposable
    private var disposable: Disposable? = null

    fun stop() {
        dispose()
    }

    fun createUser(fname: String, lname: String, tag: String) {
        Log.d(TAG, "createUser")
        val currentUser = repository.getAuthUser()
        if (currentUser != null) {
            val user = User(fname, lname, tag)
            dispose()
            disposable = repository.createUser(currentUser.uid, user)
                    .subscribe({
                        createUserCompleteEvent.value = true
                    },{ createUserCompleteEvent.value = false })
        } else {
            createUserCompleteEvent.value = false
        }
    }

    private fun dispose() {
        disposable?.let {
            if (!it.isDisposed)
                it.dispose()
        }
    }

    companion object {
        private const val TAG = "CreateUserViewModel"
    }

}
