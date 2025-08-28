package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.adapters.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.adapters.LiftSwipeItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.adapters.ReminderAdapter
import ru.vodolatskii.movies.presentation.utils.AutoDisposable
import ru.vodolatskii.movies.presentation.utils.SimpleUIState
import ru.vodolatskii.movies.presentation.utils.addTo
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class AfterFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var reminderAdapter: ReminderAdapter
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
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 2)
        setupFavoriteRV()
        setupObservers()
        viewModel.getReminderMovies()
    }

    private fun setupObservers() {
        viewModel.reminderUIState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is SimpleUIState.Success -> {
                        val mutableMoviesList = state.listMovie
                        setReminderViewsVisibility(state)
                        reminderAdapter.setData(mutableMoviesList)
                    }

                    is SimpleUIState.Error -> {
                        setReminderViewsVisibility(state)
                    }

                    is SimpleUIState.Loading -> {
                        setReminderViewsVisibility(state)
                    }
                }
            }
            .addTo(autoDisposable)
    }


    private fun setupFavoriteRV() {
        binding.recyclerViewFav.apply {
            reminderAdapter = ReminderAdapter(
                onItemClick = { movie, view ->
//                    (activity as MainActivity).launchDetailsFragment(
//                        movie,
//                        view
//                    )
                },
                onDeleteFromReminded = { movie ->
//                    viewModel.deleteMovieFromFavorite(movie)
                },

                context = requireContext()
            )

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = reminderAdapter

            val decorator = ContentRVItemDecoration(5)
            addItemDecoration(decorator)

            val anim =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.content_rv_layout_anim)

            layoutAnimation = anim
            scheduleLayoutAnimation()

            val callback = LiftSwipeItemTouchHelperCallback(reminderAdapter)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(this)

//        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(this)

        }
    }


    private fun setReminderViewsVisibility(state: SimpleUIState) {
        when (state) {
            is SimpleUIState.Success -> {
                binding.progressCircularFav.visibility = View.GONE
                binding.recyclerViewFav.visibility = View.VISIBLE
            }

            is SimpleUIState.Error -> {
                binding.progressCircularFav.visibility = View.GONE
                binding.recyclerViewFav.visibility = View.VISIBLE
            }

            SimpleUIState.Loading -> {
                binding.progressCircularFav.visibility = View.VISIBLE
                binding.recyclerViewFav.visibility = View.GONE
            }
        }
    }
}