package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MoviesViewModel
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var viewModel: MoviesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).getMoviesViewModel()
        viewModel.getFavoriteMovies()
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        initFavoriteRV()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.getFavoriteMovies()
    }

    private fun setupObservers() {
        lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.favoriteState.collect { uiState ->

                    when (uiState) {
                        is UIState.Success -> {
                            val mutableMoviesList = uiState.listMovie.toMutableList()
                            setFavoriteViewsVisibility(uiState)
                            contentAdapter.setData(mutableMoviesList)
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


    private fun initFavoriteRV() {
        binding.recyclerViewFav.apply {
            contentAdapter = ContentAdapter(
                onItemClick = { movie -> (activity as MainActivity).launchDetailsFragment(movie) },
                onLeftSwipe = { movie -> })

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = contentAdapter

            val decorator = ContentRVItemDecoration(5)
            addItemDecoration(decorator)

            val anim =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.content_rv_layout_anim)

            layoutAnimation = anim
            scheduleLayoutAnimation()

            val callback = ContentItemTouchHelperCallback(this)
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


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
            }
    }
}