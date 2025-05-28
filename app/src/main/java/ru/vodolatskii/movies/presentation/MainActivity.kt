package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc
import ru.vodolatskii.movies.data.repository.RepositoryImpl
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.HorizontalSwipeListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentAdapter: ContentAdapter

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

        initContentRecyclerView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            val mutableDocsList = uiState.listDoc.toMutableList().shuffled()
                            setPostersViewsVisibility(uiState)
                            contentAdapter.setData(mutableDocsList)
                        }

                        is UIState.Error -> {
                            val mutableDocsList = uiState.apiErrorUrlsList.toMutableList().shuffled()
                            setPostersViewsVisibility(uiState)
                            contentAdapter.setData(mutableDocsList)

                        }

                        is UIState.Loading -> {
                            setPostersViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
//        binding.buttonPostersError.setOnClickListener {
//            viewModel.loadPosters()
//        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT)
                .show()
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
            when (it.itemId) {
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
private fun initContentRecyclerView(){
    binding.recyclerviewContent.apply {
        contentAdapter = ContentAdapter(object : ContentAdapter.OnItemClickListener {
            override fun click(doc: Doc) {
                TODO("Not yet implemented")
            }
        })

        layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        adapter = contentAdapter
        val decorator = ContentRVItemDecoration(5)
        addItemDecoration(decorator)

        val anim = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.content_rv_layout_anim)

        layoutAnimation = anim
        scheduleLayoutAnimation()

        val callback = ContentItemTouchHelperCallback(contentAdapter, this@MainActivity)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(this)
    }
}

    private fun setPostersViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
            }

            is UIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.VISIBLE
//                binding.textviewErrorMessage.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerviewContent.visibility = View.GONE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
            }
        }
    }
}

