package ru.vodolatskii.movies

import android.app.Application
import android.content.res.Configuration
import com.github.ajalt.timberkt.BuildConfig
import ru.vodolatskii.movies.di.AppComponent
import ru.vodolatskii.movies.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    lateinit var dagger: AppComponent
    var loadPopularMoviesLimit: Int = 30
    var isFirstLaunch = true

    override fun onCreate() {
        super.onCreate()
        instance = this
        dagger = DaggerAppComponent.factory().create(this)

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

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

//class LifeCycleListener : LifecycleObserver {
////    @OnLifecycleEvent(Lifecycle.Event.ON_START)
////    fun start() {
////        Timber.d("on start")
////    }
////
////    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
////    fun stop() {
////        Timber.d("on stop")
////    }
//}

