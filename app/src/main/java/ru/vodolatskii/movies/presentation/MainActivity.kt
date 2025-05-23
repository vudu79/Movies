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

    val progressBar = findViewById<ProgressBar>(R.id.progress_circular)
    val posters = findViewById<CardView>(R.id.posters)


    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar.visibility = View.GONE
        posters.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            setPostersVisible(true)
                            uiState.listDoc.forEach {
                                Log.d("mytag", "результат ${it.poster.url}")
                            }
                        }

                        is UIState.Error -> Log.d("mytag", "результат error")
                        is UIState.Loading -> {
                            setPostersVisible(false)
                        }
                    }
                }
            }
        }
    }

    private fun setPostersVisible(isVisible: Boolean){
        if (isVisible){
            progressBar.visibility = View.GONE
            posters.visibility = View.VISIBLE
        }
    }


    private fun recycleViewLoad(){
        val imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        )

        val adapter = ImageAdapter(imageUrls)
        recyclerView.adapter = adapter
    }
}