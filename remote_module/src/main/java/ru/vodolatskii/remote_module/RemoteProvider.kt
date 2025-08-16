package ru.vodolatskii.remote_module

interface RemoteProvider {
    fun provideRemote(): KPApiService
}