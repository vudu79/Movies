package ru.vodolatskii.movies.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.vodolatskii.movies.presentation.LaunchActivity
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.viewmodels.ViewModelFactory
import ru.vodolatskii.remote_module.RemoteProvider
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [RemoteProvider::class],
    modules = [
        DomainModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {

    fun viewModelsFactory(): ViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            remoteProvider: RemoteProvider
        ): AppComponent
    }

    fun inject(activityMain: MainActivity)
    fun inject(activityLaunch: LaunchActivity)
}