package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.util.Log
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
import ru.vodolatskii.movies.presentation.utils.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.PostersAdapter
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

//        setPostersViewsVisibility(UIState.Loading)

//        binding.recyclerViewPosters.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewContent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
//                            setPostersViewsVisibility(uiState)

//                            val postersAdapter = PostersAdapter(docs = uiState.listDoc.shuffled())
//                            binding.recyclerViewPosters.adapter = postersAdapter

                            val contentAdapter = ContentAdapter(docs = uiState.listDoc.shuffled()){
                                Toast.makeText(this@MainActivity, it.name, Toast.LENGTH_SHORT).show()

                            }
                            binding.recyclerViewContent.adapter = contentAdapter
                        }

                        is UIState.Error -> {
//                            setPostersViewsVisibility(uiState)
                            val adapter = PostersAdapter(errorUrls = uiState.apiErrorUrlsList)
//                            binding.recyclerViewPosters.adapter = adapter
                            Log.d("mytag", "Ошибка запроса, подставил заглушку")
                        }

                        is UIState.Loading -> {
//                            setPostersViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }


//    private fun imageAnimation(state: UIState.Success) {
//
//        state.listDoc.take(4).forEach {
//            val image = ImageView(this).apply {
//                layoutParams = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.MATCH_PARENT
//                )
//
//            }
//            Glide.with(this)
//                .load(it.poster.url)
//                .override(200, 200)
//                .centerCrop()
//                .into(image)
//
//            val cardView = CardView(this).apply {
//                radius = 25f
//                layoutParams = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.WRAP_CONTENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT
//                )
//                scaleX = 0f
//                scaleY = 0f
//            }
//            cardView.addView(image)
//
//            binding.imageContainer.layoutTransition.setAnimator(LayoutTransition.APPEARING, AnimatorInflater.loadAnimator(this, R.animator.image_animatior))
//            binding.imageContainer.addView(cardView)
//
//        }
//    }

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


//    private fun setPostersViewsVisibility(state: UIState) {
//        when (state) {
//            is UIState.Success -> {
//                binding.progressCircular.visibility = View.GONE
//                binding.recyclerViewPosters.visibility = View.VISIBLE
////                binding.buttonPostersError.visibility = View.GONE
////                binding.textviewErrorMessage.visibility = View.GONE
//            }
//
//            is UIState.Error -> {
//                binding.progressCircular.visibility = View.GONE
//                binding.recyclerViewPosters.visibility = View.VISIBLE
////                binding.buttonPostersError.visibility = View.VISIBLE
////                binding.textviewErrorMessage.visibility = View.VISIBLE
//            }
//
//            UIState.Loading -> {
//                binding.progressCircular.visibility = View.VISIBLE
//                binding.recyclerViewPosters.visibility = View.GONE
////                binding.buttonPostersError.visibility = View.GONE
////                binding.textviewErrorMessage.visibility = View.GONE
//            }
//        }
//    }
}

