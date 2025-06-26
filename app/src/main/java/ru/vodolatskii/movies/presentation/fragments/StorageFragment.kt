package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ru.vodolatskii.movies.databinding.FragmentStorageBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.utils.CustomListViewAdapter
import ru.vodolatskii.movies.presentation.utils.DataModel
import ru.vodolatskii.movies.presentation.utils.StorageSearchEvent
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

class StorageFragment : Fragment() {

    private lateinit var binding: FragmentStorageBinding
    lateinit var contentAdapter: ContentAdapter
    private lateinit var viewModel: MoviesViewModel

    private var dataModel: ArrayList<DataModel>? = null
    private lateinit var listView: ListView
    private lateinit var adapter: CustomListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStorageBinding.inflate(
            inflater,
            container,
            false
        )
        viewModel = (activity as MainActivity).shareMoviesViewModel()
        listView = binding.listView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupContentRV()
        setupObservers()
        setupListeners()
        setupListView()
        viewModel.getMovieCountFromDB()


    }

    private fun setupListView() {
        dataModel = ArrayList<DataModel>()
        dataModel!!.add(DataModel(Pair(28, "Action"), false))
        dataModel!!.add(DataModel(Pair(12, "Adventure"), false))
        dataModel!!.add(DataModel(Pair(16, "Animation"), false))
        dataModel!!.add(DataModel(Pair(35, "Comedy"), false))
        dataModel!!.add(DataModel(Pair(80, "Crime"), false))
        dataModel!!.add(DataModel(Pair(80, "Crime"), false))
        dataModel!!.add(DataModel(Pair(99, "Documentary"), false))
        dataModel!!.add(DataModel(Pair(18, "Drama"), false))
        dataModel!!.add(DataModel(Pair(10751, "Family"), false))
        dataModel!!.add(DataModel(Pair(14, "Fantasy"), false))
        dataModel!!.add(DataModel(Pair(36, "History"), false))
        dataModel!!.add(DataModel(Pair(27, "Horror"), false))
        dataModel!!.add(DataModel(Pair(10402, "Music"), false))
        dataModel!!.add(DataModel(Pair(9648, "Mystery"), false))
        dataModel!!.add(DataModel(Pair(10749, "Romance"), false))
        dataModel!!.add(DataModel(Pair(878, "Science Fiction"), false))
        dataModel!!.add(DataModel(Pair(10770, "TV Movie"), false))
        dataModel!!.add(DataModel(Pair(53, "Thriller"), false))
        dataModel!!.add(DataModel(Pair(10752, "War"), false))
        dataModel!!.add(DataModel(Pair(37, "Western"), false))

        adapter = CustomListViewAdapter(dataModel!!, requireContext()) { position ->
            val dataItem: DataModel = dataModel!![position] as DataModel
            dataItem.checked = !dataItem.checked
            adapter.notifyDataSetChanged()
        }
        listView.adapter = adapter
    }

    private fun setupListeners() {
        binding.buttonSearch.setOnClickListener {
            viewModel.onStorageSearchEvent(
                StorageSearchEvent(
                    rating = binding.editTextRating.text.toString(),
                    date = binding.editTextDate.text.toString(),
                    title = binding.editTextTitle.text.toString(),
                    genres = dataModel!!.filter { it.checked }
                        .map { it.pier.first }
                )
            )
        }
    }

    private fun setupObservers() {
        viewModel.movieCountInDBModeData.observe(viewLifecycleOwner, Observer<Int> {
            binding.textViewMovieCount.text = it.toString()
        })
    }
}