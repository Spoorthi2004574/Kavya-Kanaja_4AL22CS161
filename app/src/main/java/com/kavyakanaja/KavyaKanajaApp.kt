package com.kavyakanaja

import android.app.Application
import com.kavyakanaja.data.PoemRepositoryImpl
import com.kavyakanaja.domain.PoemRepository

class KavyaKanajaApp : Application() {

    lateinit var poemRepository: PoemRepository
        private set

    override fun onCreate() {
        super.onCreate()
        poemRepository = PoemRepositoryImpl(applicationContext)
    }
}
