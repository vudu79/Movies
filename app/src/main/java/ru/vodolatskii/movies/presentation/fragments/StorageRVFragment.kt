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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentStorageRvBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.utils.AutoDisposable
import ru.vodolatskii.movies.presentation.utils.UIStateStorage
import ru.vodolatskii.movies.presentation.utils.addTo
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.utils.contentRV.FavoriteItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

class StorageRVFragment : Fragment() {
    private lateinit var binding: FragmentStorageRvBinding
    private lateinit var storageAdapter: ContentAdapter
    private lateinit var viewModel: MoviesViewModel
    private val autoDisposable = AutoDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).shareMoviesViewModel()
        binding = FragmentStorageRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 2)
        setupStorageRV()
        setupObservers()
    }

//    private fun checkToolBar(){
//
//        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
//            binding.favoriteSearchView.visibility = if (state) View.VISIBLE else View.GONE
//            when(state){
//                true-> {
//                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
//                        View.GONE
//                }
//                false -> {
//                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
//                        View.VISIBLE
//                }
//            }
//        }
//    }


//    private fun setupSearchViewListeners() {
//        val icon =
//            binding.favoriteSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
//        icon.setImageResource(R.drawable.baseline_search_24)
//
//        val closeButton =
//            binding.favoriteSearchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
//        closeButton.setImageResource(R.drawable.baseline_close_24)
//
//        binding.favoriteSearchView.queryHint = "Search movie"
//
//        binding.favoriteSearchView.setOnClickListener {
//            binding.favoriteSearchView.isIconified = false
//        }
//
//        binding.favoriteSearchView.setOnCloseListener {
//            viewModel.switchSearchViewVisibility(false)
//            false
//        }
//
//        binding.favoriteSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                if (newText.isEmpty()) {
//                    storageAdapter.setData(viewModel.cachedFavoriteMovieList)
//                    return true
//                }
//                val result = viewModel.cachedFavoriteMovieList.filter {
//                    it.title.toLowerCase(Locale.getDefault())
//                        .contains(newText.toLowerCase(Locale.getDefault()))
//                }
//                storageAdapter.setData(result)
//                return true
//            }
//        })
//    }


    private fun setupObservers() {
        viewModel.storageUIState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { uiState ->
                when (uiState) {
                    is UIStateStorage.Success -> {
                        val mutableMoviesList = uiState.listMovie.toMutableList()
                        setStorageViewsVisibility(uiState)
                        storageAdapter.setData(mutableMoviesList)
                    }

                    is UIStateStorage.Error -> {
                        binding.storageErrorTextView.text = uiState.message
                        setStorageViewsVisibility(uiState)
                    }

                    is UIStateStorage.Loading -> {
                        setStorageViewsVisibility(uiState)
                    }
                }
            }
            .addTo(autoDisposable)
    }


    private fun setupStorageRV() {
        binding.recyclerViewStorage.apply {
            storageAdapter = ContentAdapter(
                onItemClick = { movie, view ->
                    (activity as MainActivity).launchDetailsFragment(
                        movie,
                        view
                    )
                },
                onMoveToFavorite = {},
                onDeleteFromFavorite = {},
                onDeleteFromPopular = {},
                context = requireContext(),
            )

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = storageAdapter

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

    private fun setStorageViewsVisibility(state: UIStateStorage) {
        when (state) {
            is UIStateStorage.Success -> {
                binding.recyclerViewStorage.visibility = View.VISIBLE
                binding.progressCircularStorage.visibility = View.GONE
                binding.storageErrorTextView.visibility = View.GONE

            }

            is UIStateStorage.Error -> {
                binding.progressCircularStorage.visibility = View.GONE
                binding.recyclerViewStorage.visibility = View.GONE
                binding.storageErrorTextView.visibility = View.VISIBLE
            }

            UIStateStorage.Loading -> {
                binding.progressCircularStorage.visibility = View.VISIBLE
                binding.recyclerViewStorage.visibility = View.GONE
                binding.storageErrorTextView.visibility = View.GONE
            }
        }
    }
}