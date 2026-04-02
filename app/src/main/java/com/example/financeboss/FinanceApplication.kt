package com.example.financeboss



import android.app.Application
import com.example.financeboss.di.AppContainer


class FinanceApplication : Application(){
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}