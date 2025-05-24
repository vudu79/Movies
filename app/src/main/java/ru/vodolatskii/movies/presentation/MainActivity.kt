package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.repository.RepositoryImpl
import ru.vodolatskii.movies.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel: KPViewModel by lazy {
        val factory = MyViewModelFactory(RepositoryImpl())
        ViewModelProvider(this, factory).get(KPViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        initClickListeners()
        setPostersViewsVisibility(UIState.Loading)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {

                        is UIState.Success -> {
                            setPostersViewsVisibility(uiState)
                            val adapter = ImageAdapter(uiState.listDoc.shuffled()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "нажат ${it.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            binding.recyclerView.adapter = adapter
                        }

                        is UIState.Error -> {
                            binding.textviewErrorMessage.text = uiState.message
                            setPostersViewsVisibility(uiState)
                            Log.d("mytag", "результат error")
                        }

                        is UIState.Loading -> {
                            setPostersViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        val buttonList = listOf(
            binding.buttonMenu,
            binding.buttonFavorites,
            binding.buttonAfter,
            binding.buttonCollections,
            binding.buttonSettings
        )

        buttonList.forEach { b ->
            b.setOnClickListener {
                Toast.makeText(this, b.text, Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonPostersError.setOnClickListener {
            viewModel.loadPosters()
        }
    }


    private fun setPostersViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.buttonPostersError.visibility = View.GONE
                binding.textviewErrorMessage.visibility = View.GONE
            }

            is UIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.buttonPostersError.visibility = View.VISIBLE
                binding.textviewErrorMessage.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.buttonPostersError.visibility = View.GONE
                binding.textviewErrorMessage.visibility = View.GONE
            }
        }

    }
}

