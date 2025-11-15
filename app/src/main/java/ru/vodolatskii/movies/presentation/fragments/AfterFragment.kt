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
import com.example.myapp.AlarmHelper
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentAfterBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.adapters.ContentRVItemDecoration
import ru.vodolatskii.movies.presentation.adapters.LiftSwipeItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.adapters.ReminderAdapter
import ru.vodolatskii.movies.presentation.utils.AutoDisposable
import ru.vodolatskii.movies.presentation.utils.DateTimeHelper
import ru.vodolatskii.movies.presentation.utils.SimpleUIState
import ru.vodolatskii.movies.presentation.utils.addTo
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class AfterFragment : Fragment() {
    private lateinit var binding: FragmentAfterBinding
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var viewModel: MoviesViewModel
    private val autoDisposable = AutoDisposable()
    private lateinit var alarmHelper: AlarmHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).shareMoviesViewModel()
        binding = FragmentAfterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 2)
        setupReminderRV()
        setupObservers()
        alarmHelper = AlarmHelper(requireContext())
        viewModel.getReminderMovies()
    }

    private fun setupObservers() {
        viewModel.reminderUIState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is SimpleUIState.Success -> {
                        setReminderViewsVisibility(state)
                        reminderAdapter.setData(state.listMovie)
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


    private fun setupReminderRV() {
        binding.recyclerViewAfter.apply {
            reminderAdapter = ReminderAdapter(
                onEditButtonClick = { movie ->
                    DateTimeHelper.showDateTimePicker(requireActivity()) { millis, str ->
                        if (!movie.isFavorite) viewModel.updateReminderForMovie(
                            movie.apiId,
                            true,
                            millis,
                            str
                        )
                        viewModel.getReminderMovies()

                        Snackbar.make(
                            this,
                            "Напомнить ${str} ",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction(R.string.ok) {}
                            .show()
                    }
                },
                onDeleteFromReminded = { movie ->
                   viewModel.updateReminderForMovie(movie.apiId, false,0L,"")
                    alarmHelper.canselAlarm(
                        movie
                    )
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
                binding.recyclerViewAfter.visibility = View.VISIBLE
            }

            is SimpleUIState.Error -> {
                binding.recyclerViewAfter.visibility = View.VISIBLE
            }

            SimpleUIState.Loading -> {
                binding.recyclerViewAfter.visibility = View.GONE
            }
        }
    }
}