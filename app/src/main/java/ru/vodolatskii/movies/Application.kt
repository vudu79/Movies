package ru.vodolatskii.movies

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import ru.vodolatskii.movies.common.WorkManagerHelper
import ru.vodolatskii.movies.di.AppComponent
import ru.vodolatskii.movies.di.DaggerAppComponent
import ru.vodolatskii.remote_module.DaggerRemoteComponent
import timber.log.Timber
import java.util.UUID


class App : Application() {

    lateinit var dagger: AppComponent
    private lateinit var preference: SharedPreferences
    private lateinit var workManagerHelper: WorkManagerHelper

    var loadPopularMoviesLimit: Int = 3

    override fun onCreate() {
        super.onCreate()
        instance = this
        preference = this.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        workManagerHelper = WorkManagerHelper(this)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        daggerSetup()
        timberSetup()
        workManagerHelper.startPeriodicWork()
    }

    private fun daggerSetup() {
        val remoteProvider = DaggerRemoteComponent.create()
        dagger = DaggerAppComponent.factory().create(this, remoteProvider)
    }

    private fun timberSetup() {
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                super.log(priority, "vudu $tag", message, t)
            }

            override fun createStackElementTag(element: StackTraceElement): String {
                return " ${super.createStackElementTag(element)}: ${element.methodName}:${element.lineNumber}"
            }
        })
    }


    // Вызывается при изменении конфигурации, например, поворот
// Этот метод тоже не обязателен к предопределению
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // Этот метод вызывается, когда у системы остается мало оперативной памяти
// и система хочет, чтобы запущенные приложения поумерили аппетиты
// Переопределять необязательно
    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    companion object {
        lateinit var instance: App
            private set
        private const val SP_FILE_NAME = "settings"
        private const val DEVICE_UUID = "\"device_uuid\""
    }
}


