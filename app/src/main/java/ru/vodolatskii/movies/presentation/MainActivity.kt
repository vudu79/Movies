package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.repository.RepositoryImpl

class MainActivity : AppCompatActivity() {

    val viewModel: KPViewModel by lazy {
        val factory = MyViewModelFactory(RepositoryImpl())
        ViewModelProvider(this, factory).get(KPViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> Log.d("mytag", "результат ${uiState.title}")
                        is UIState.Error -> Log.d("mytag", "результат error")
                        is UIState.Loading -> Log.d("mytag", "результат loading")
                    }
                }
            }
        }
    }
}