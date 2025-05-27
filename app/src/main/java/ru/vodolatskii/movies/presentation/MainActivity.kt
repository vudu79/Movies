package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.repository.RepositoryImpl
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.utils.ImageAdapter
import ru.vodolatskii.movies.presentation.utils.UIState

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

        setClickListeners()

        setPostersViewsVisibility(UIState.Loading)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            setPostersViewsVisibility(uiState)
                            val adapter = ImageAdapter(docs = uiState.listDoc.shuffled()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Клик по постеру - ${it.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            binding.recyclerView.adapter = adapter
                        }

                        is UIState.Error -> {
                            setPostersViewsVisibility(uiState)
                            val adapter = ImageAdapter(errorUrls = uiState.apiErrorUrlsList) {}
                            binding.recyclerView.adapter = adapter
                            Log.d("mytag", "Ошибка запроса, подставил заглушку")
                        }

                        is UIState.Loading -> {
                            setPostersViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }


    private fun imageAnimation(state: UIState.Success) {

        state.listDoc.take(6).forEach {
            val image = ImageView(this)
            Glide.with(this)
                .load(it.poster.url)
                .override(200, 200)
                .centerCrop()
                .into(image)

            val cardView = CardView(this).apply {
                radius = 15f
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }
            cardView.addView(image)
//            binding.imageContainer.addView(cardView)

        }
    }

    private fun setClickListeners() {
//        binding.buttonPostersError.setOnClickListener {
//            viewModel.loadPosters()
//        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT).show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.button_search -> {
                    Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть похже", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }


    private fun setPostersViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
            }

            is UIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.VISIBLE
//                binding.textviewErrorMessage.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
            }
        }
    }
}

