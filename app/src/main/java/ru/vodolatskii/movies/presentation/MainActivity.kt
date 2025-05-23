package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val progressBar = findViewById<ProgressBar>(R.id.progress_circular)

//    val posters = findViewById<CardView>(R.id.posters)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {

                        is UIState.Success -> {

                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE


                            uiState.listDoc.forEach {

                                val adapter = ImageAdapter(uiState.listDoc)
                                recyclerView.adapter = adapter

                                Log.d("mytag", "результат ${it.poster.url }")
                            }
                        }

                        is UIState.Error -> Log.d("mytag", "результат error")
                        is UIState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE

                        }
                    }
                }
            }
        }
    }


}