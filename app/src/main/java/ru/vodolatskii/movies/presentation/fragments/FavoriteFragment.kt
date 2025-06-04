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
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MoviesViewModel
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.utils.contentRV.FavoriteItemTouchHelperCallback


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
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        initFavoriteRV()
        if (activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility == View.GONE) {
            activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                View.VISIBLE
        }

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
                            val mutableMoviesList = uiState.listMovie
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
                onMoveToFavorite = { movie -> },
                onDeleteFromFavorite = { movie ->
                    viewModel.deleteMovieFromFavorite(movie)
                }
            )

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = contentAdapter

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


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
            }
    }
}