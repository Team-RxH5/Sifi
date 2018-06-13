package com.anagramsoftware.sifi.di

import com.anagramsoftware.sifi.data.source.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module.module

val commonModule = module {

    factory { FirebaseAuth.getInstance() }

    factory { FirebaseFirestore.getInstance() }

    bean { Repository(get(), get()) }

}