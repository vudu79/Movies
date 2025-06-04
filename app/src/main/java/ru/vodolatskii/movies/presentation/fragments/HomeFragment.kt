package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.databinding.FragmentHomeBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MoviesViewModel
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


internal interface ContentAdapterController {
    fun updateAdapterData(data: List<Movie>?)
}


class HomeFragment : Fragment(), ContentAdapterController {

    private lateinit var binding: FragmentHomeBinding
    lateinit var contentAdapter: ContentAdapter
    private val viewModel: MoviesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        initContentRV()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupSearchViewListeners()
        checkToolBar()
    }

    private fun checkToolBar() {
        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
            when (state) {
                true -> {
                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility = View.GONE
                }
                false -> {
                    if (activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility == View.GONE) {
                        activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                            View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupSearchViewListeners() {

        val icon =
            binding.homeSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        icon.setImageResource(R.drawable.baseline_search_24)

        val closeButton =
            binding.homeSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setImageResource(R.drawable.baseline_close_24)

        binding.homeSearchView.queryHint = "Search movie"

        binding.homeSearchView.setOnClickListener {
            binding.homeSearchView.isIconified = false
        }

        binding.homeSearchView.setOnCloseListener {
            viewModel.switchSearchViewVisibility(false)
            false
        }

        binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    contentAdapter.setData(viewModel.cachePopularMovieList)
                    return true
                }
                val result = viewModel.cachePopularMovieList.filter {
                    it.name.toLowerCase(Locale.getDefault())
                        .contains(newText.toLowerCase(Locale.getDefault()))
                }
                contentAdapter.setData(result)
                return true
            }
        })
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                (activity as MainActivity).viewModel.homeState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            val mutableMoviesList = uiState.listMovie
                            setHomeViewsVisibility(uiState)
                            contentAdapter.setData(mutableMoviesList)
                        }

                        is UIState.Error -> {
                            setHomeViewsVisibility(uiState)
                        }

                        is UIState.Loading -> {
                            setHomeViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
            binding.homeSearchView.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    private fun initContentRV() {

        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
                    if (state) {
                        if (dy > 0) {
                            binding.homeSearchView.visibility = View.VISIBLE
                        } else if (dy < 0) {
                            binding.homeSearchView.visibility = View.GONE
                        } else {
                            binding.homeSearchView.visibility = View.VISIBLE
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

        binding.recyclerviewContent.addOnScrollListener(onScrollListener)

        binding.recyclerviewContent.apply {
            contentAdapter = ContentAdapter(
                onItemClick = { movie ->
                    (activity as MainActivity).launchDetailsFragment(movie)
                    if (activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility == View.GONE) {
                        activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                            View.VISIBLE
                    }
                },
                onMoveToFavorite = { movie ->
                    viewModel.addMovieToFavorite(movie.copy(isFavorite = true))
                },
                onDeleteFromFavorite = {})

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

//            val pagerSnapHelper = PagerSnapHelper()
//            pagerSnapHelper.attachToRecyclerView(this)

            val linearSnapHelper = LinearSnapHelper()
            linearSnapHelper.attachToRecyclerView(this)

        }
    }


    private fun setHomeViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
            }

            is UIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerviewContent.visibility = View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun updateAdapterData(data: List<Movie>?) {
        if (data != null) {
            contentAdapter.setData(data)
        }
    }
}