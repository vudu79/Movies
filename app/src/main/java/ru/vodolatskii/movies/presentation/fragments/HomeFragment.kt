package ru.vodolatskii.movies.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentHomeBinding
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.utils.AnimationHelper
import ru.vodolatskii.movies.presentation.utils.AutoDisposable
import ru.vodolatskii.movies.presentation.utils.HomeUIState
import ru.vodolatskii.movies.presentation.utils.addTo
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


internal interface ContentAdapterController {
    fun updateAdapterData(data: List<Movie>?)
}

class HomeFragment : Fragment(), ContentAdapterController {

    private lateinit var binding: FragmentHomeBinding
    lateinit var contentAdapter: ContentAdapter
    private var isContentSourceApi: Boolean = true
    private lateinit var viewModel: MoviesViewModel
    private val autoDisposable = AutoDisposable()


//    init {
//        exitTransition = Fade(Fade.MODE_OUT).apply { duration = 500 }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).shareMoviesViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (App.instance.isFirstLaunch) {
            App.instance.isFirstLaunch = false
            view.setBackgroundResource(R.color.black)
            view.visibility = View.VISIBLE
        } else {
            AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 1)
        }

        setupContentRV()
        setupObservers()
        setupListeners()
        checkToolBar()
        showMovies()
        initSpeedDial(savedInstanceState == null)
    }

    private fun showMovies() {
        viewModel.getMoviesFromApi()
    }

    private fun checkToolBar() {
        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
            when (state) {
                true -> {
                    activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                        View.GONE
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

    private fun setupListeners() {

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

//        binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//
//                when (val list = viewModel.computedUIState.value) {
//                    is UIState.Success -> {
//                       if (newText.isEmpty()) {
//                           contentAdapter.setData(viewModel.cachedMovieList.value ?: emptyList())
//                           return true
//                       }
//                      val res = list.listMovie.filter {
//                           it.title.toLowerCase(Locale.getDefault())
//                               .contains(newText.toLowerCase(Locale.getDefault()))
//                       } ?: emptyList()
//                       contentAdapter.setData(res)
//                        return true
//                    }
//
//                    is UIState.Error -> TODO()
//                    UIState.Loading -> TODO()
//                }
//                return true
//            }
//        })

        binding.pullToRefresh.setOnRefreshListener {
            contentAdapter.setData(emptyList())
            viewModel.clearLoadedPages()
            viewModel.getMoviesFromApi()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.homeUIState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is HomeUIState.Loading -> setHomeViewsVisibility(state)

                    is HomeUIState.Success -> {
                        contentAdapter.updateData(state.movie)
                        setHomeViewsVisibility(state)
                    }

                    is HomeUIState.Error -> {
                        binding.errorTextView.text = state.message
                        setHomeViewsVisibility(state)
                    }
                }
            }
            .addTo(autoDisposable)


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                (activity as MainActivity).viewModel.messageSingleLiveEvent.observe(
                    viewLifecycleOwner
                ) { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { state ->
            binding.homeSearchView.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    private fun setupContentRV() {

        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
//                val totalItemCount = layoutManager.itemCount
//
//                currentPosition =
//                    if (currentPosition != lastVisibleItem) lastVisibleItem else currentPosition
//
//                val diff = totalItemCount - currentPosition
//                val trigger = totalItemCount / 100 * 10
//
//                if (diff == trigger) {
//                    viewModel.plusPageCount()
//                    viewModel.getPopularMovies()
//                }

                if (!recyclerView.canScrollVertically(1)) {
                    Log.d("mytag", "scroll")

                    viewModel.plusPageCount()

                    viewModel.getMoviesFromApi()
                }

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

                    }
                }
            }
        }

        binding.recyclerviewContent.apply {
            addOnScrollListener(onScrollListener)

            contentAdapter = ContentAdapter(
                context = requireContext(),
                onItemClick = { movie, view ->
                    (activity as MainActivity).launchDetailsFragment(movie, view)
                    if (activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility == View.GONE) {
                        activity?.findViewById<AppBarLayout>(R.id.topAppBarLayout)?.visibility =
                            View.VISIBLE
                    }
                },
                onMoveToFavorite = { movie ->
//                    viewModel.addMovieToFavorite(movie.copy(isFavorite = true))
                },
                onDeleteFromFavorite = {},
                onDeleteFromPopular = { movie ->
//                    viewModel.deleteFromCachedList(movie = movie)
                },
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

            val callback = ContentItemTouchHelperCallback(this)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)

//            val pagerSnapHelper = PagerSnapHelper()
//            pagerSnapHelper.attachToRecyclerView(this)

            val linearSnapHelper = LinearSnapHelper()
            linearSnapHelper.attachToRecyclerView(this)

        }
    }


    private fun setHomeViewsVisibility(state: HomeUIState) {
        when (state) {
            is HomeUIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.GONE
                binding.errorTextView.visibility = View.VISIBLE
            }

            HomeUIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerviewContent.visibility = View.GONE
                binding.errorTextView.visibility = View.GONE
            }

            is HomeUIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
                binding.errorTextView.visibility = View.GONE
            }
        }
    }

    override fun updateAdapterData(data: List<Movie>?) {
        if (data != null) {
            contentAdapter.setData(data)
        }
    }


    private fun initSpeedDial(addActionItems: Boolean) {
        val speedDialView: SpeedDialView = binding.speedDial
        if (addActionItems) {
            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.sort_alph, R.drawable.baseline_sort_by_alpha_24,
                )
                    .setFabImageTintColor(Color.WHITE)
                    .setFabBackgroundColor(requireContext().getColor(R.color.gradient_end))
                    .setFabSize(FloatingActionButton.SIZE_NORMAL)
                    .setLabel(R.string.alphabet)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(requireContext().getColor(R.color.gradient_end2))
                    .create(),
            )

            val drawable =
                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_date_range_24)
            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.sort_date, drawable,
                )
                    .setFabImageTintColor(Color.WHITE)
                    .setFabBackgroundColor(requireContext().getColor(R.color.gradient_end))
                    .setFabSize(FloatingActionButton.SIZE_NORMAL)
                    .setLabel(R.string.date)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(requireContext().getColor(R.color.gradient_end2))
                    .create()
            )

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id.sort_rating, R.drawable.baseline_auto_graph_24,
                )
                    .setFabImageTintColor(Color.WHITE)
                    .setFabBackgroundColor(requireContext().getColor(R.color.gradient_end))
                    .setFabSize(FloatingActionButton.SIZE_NORMAL)
                    .setLabel(R.string.rating)
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(requireContext().getColor(R.color.gradient_end2))
                    .create(),
            )
        }

        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                return false  // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
            }
        })

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.sort_alph -> {
//                    viewModel.onSortRVEvents(SortEvents.ALPHABET)
                    speedDialView.close()  // To close the Speed Dial with animation
                    return@OnActionSelectedListener true  // false will close it without animation
                }

                R.id.sort_date -> {
//                    viewModel.onSortRVEvents(SortEvents.DATE)
                    speedDialView.close()  // To close the Speed Dial with animation
                    return@OnActionSelectedListener true
                }

                R.id.sort_rating -> {
//                    viewModel.onSortRVEvents(SortEvents.RATING)
                    speedDialView.close()  // To close the Speed Dial with animation
                    return@OnActionSelectedListener true
                }
            }
            true  // To keep the Speed Dial open
        })
    }
}