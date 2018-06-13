package com.anagramsoftware.sifi.data.source

import com.anagramsoftware.sifi.data.model.User
import com.anagramsoftware.sifi.extension.mainThreadSafely
import com.anagramsoftware.sifi.util.RxFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class Repository(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    object Collections {
        const val USER = "user"
    }

    /**
     * Get currently signed in firebase user
     * */
    fun getAuthUser() = firebaseAuth.currentUser

    /**
     * Create User entry of current user on sign up
     *
     * @param uid The given uid
     * */
    fun createUser(uid: String, user: User): Completable {
        val ref = firestore.collection(Collections.USER)
        return RxFirestore.setDocument(ref, uid, user)
                    .mainThreadSafely()
    }


    /**
     * Get User for a given uid main thread safely
     *
     * @param uid The given uid
     * */
    fun getUser(uid: String): Maybe<User> {
        val ref = firestore.collection(Collections.USER).document(uid)
        return RxFirestore.getDocument(ref, User::class.java)
                .mainThreadSafely()
    }

    /**
     * Check availability of User for a given uid main thread safely
     *
     * @param uid The given uid
     * */
    fun checkUser(uid: String): Single<Boolean> {
        val ref = firestore.collection(Collections.USER)
        return RxFirestore.checkDocument(ref, uid)
                .mainThreadSafely()
    }

    /**
     * Get currenty signed in user a User main thread safely
     * */
    fun getCurrentUser() : Maybe<User>{
        val user = getAuthUser()
        return if (user != null)
            getUser(user.uid)
        else
            Maybe.empty()
    }
}