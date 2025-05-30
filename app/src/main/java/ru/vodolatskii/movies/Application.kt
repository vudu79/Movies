package ru.vodolatskii.movies

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LifecycleObserver
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        // Этот метод вызывается при старте приложения до того, как будут созданы другие компоненты приложения
// Этот метод необязательно переопределять, но это самое хорошее место для инициализации глобальных объектов

        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
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

    override fun onTrimMemory(level: Int){
        super.onTrimMemory(level)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
class LifeCycleListener : LifecycleObserver {
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun start() {
//        Timber.d("on start")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun stop() {
//        Timber.d("on stop")
//    }
}

