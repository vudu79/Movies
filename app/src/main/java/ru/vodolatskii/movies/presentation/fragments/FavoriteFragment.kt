package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MoviesViewModel
import ru.vodolatskii.movies.presentation.utils.AnimationHelper
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.utils.contentRV.FavoriteItemTouchHelperCallback
import java.util.Locale


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: ContentAdapter
    private lateinit var viewModel: MoviesViewModel

//    init {
//        exitTransition = Fade(Fade.MODE_OUT).apply { duration = 500 }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).getMoviesViewModel()
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 2)
        setupFavoriteRV()
        setupObservers()
        setupSearchViewListeners()
        checkToolBar()
        viewModel.getFavoriteMovies()
    }

    private fun checkToolBar(){

        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
            binding.favoriteSearchView.visibility = if (state) View.VISIBLE else View.GONE
            when(state){
                true-> {
                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                        View.GONE
                }
                false -> {
                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                        View.VISIBLE
                }
            }
        }
    }


    private fun setupSearchViewListeners() {
        val icon =
            binding.favoriteSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        icon.setImageResource(R.drawable.baseline_search_24)

        val closeButton =
            binding.favoriteSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setImageResource(R.drawable.baseline_close_24)

        binding.favoriteSearchView.queryHint = "Search movie"

        binding.favoriteSearchView.setOnClickListener {
            binding.favoriteSearchView.isIconified = false
        }

        binding.favoriteSearchView.setOnCloseListener {
            viewModel.switchSearchViewVisibility(false)
            false
        }

        binding.favoriteSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    favoriteAdapter.setData(viewModel.cacheFavoriteMovieList)
                    return true
                }
                val result = viewModel.cacheFavoriteMovieList.filter {
                    it.name.toLowerCase(Locale.getDefault())
                        .contains(newText.toLowerCase(Locale.getDefault()))
                }
                favoriteAdapter.setData(result)
                return true
            }
        })
    }


    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            val mutableMoviesList = uiState.listMovie
                            setFavoriteViewsVisibility(uiState)
                            favoriteAdapter.setData(mutableMoviesList)
                        }

                        is UIState.Error -> {
                            setFavoriteViewsVisibility(uiState)
                        }

                        is UIState.Loading -> {
                            setFavoriteViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }


    private fun setupFavoriteRV() {
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
                    if (state) {
                        if (dy > 0) {
                            binding.favoriteSearchView.visibility = View.VISIBLE
                        } else if (dy < 0) {
                            binding.favoriteSearchView.visibility = View.GONE
                        } else {
                            binding.favoriteSearchView.visibility = View.VISIBLE
                        }
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // Прокрутка завершается
                    }
                }
            }
        }

        binding.recyclerViewFav.addOnScrollListener(onScrollListener)

        binding.recyclerViewFav.apply {
            favoriteAdapter = ContentAdapter(
                onItemClick = { movie, view -> (activity as MainActivity).launchDetailsFragment(movie, view) },
                onMoveToFavorite = { movie -> },
                onDeleteFromFavorite = { movie ->
                    viewModel.deleteMovieFromFavorite(movie)
                },
                onDeleteFromPopular = {},
                context = requireContext(),
            )

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = favoriteAdapter

            val decorator = ContentRVItemDecoration(5)
            addItemDecoration(decorator)

            val anim =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.content_rv_layout_anim)

            layoutAnimation = anim
            scheduleLayoutAnimation()

            val callback = FavoriteItemTouchHelperCallback(this)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(this)

//        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(this)

        }
    }


    private fun setFavoriteViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircularFav.visibility = View.GONE
                binding.recyclerViewFav.visibility = View.VISIBLE
            }

            is UIState.Error -> {
                binding.progressCircularFav.visibility = View.GONE
                binding.recyclerViewFav.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircularFav.visibility = View.VISIBLE
                binding.recyclerViewFav.visibility = View.GONE
            }
        }
    }
}