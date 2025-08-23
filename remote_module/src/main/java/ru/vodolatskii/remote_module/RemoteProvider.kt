package ru.vodolatskii.remote_module

interface RemoteProvider {
    fun provideRemoteKP(): KPApiService
    fun provideRemoteSun(): SunSetApiService
}