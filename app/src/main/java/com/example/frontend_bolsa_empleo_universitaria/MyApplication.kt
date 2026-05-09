package com.example.frontend_bolsa_empleo_universitaria

import android.app.Application
import com.example.frontend_bolsa_empleo_universitaria.interfaces.RetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}