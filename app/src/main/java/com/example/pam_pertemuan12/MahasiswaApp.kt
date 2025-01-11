package com.example.pam_pertemuan12

import android.app.Application
import com.example.pam_pertemuan12.di.MahasiswaContainer


class MahasiswaApp : Application() {

    lateinit var containerApp: MahasiswaContainer
    override fun onCreate() {
        super.onCreate()

        containerApp = MahasiswaContainer(this)
    }
}