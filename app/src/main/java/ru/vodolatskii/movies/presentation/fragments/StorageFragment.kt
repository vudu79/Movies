package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentHomeBinding
import ru.vodolatskii.movies.databinding.FragmentStorageBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.utils.AnimationHelper
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

class StorageFragment : Fragment() {

    private lateinit var binding: FragmentStorageBinding
    lateinit var contentAdapter: ContentAdapter
    private lateinit var viewModel: MoviesViewModel

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupContentRV()
        setupObservers()
        setupSearchViewListeners()
        viewModel.getMovieCountFromDB()
    }

    private fun setupSearchViewListeners() {
    }

    private fun setupObservers() {
        viewModel.movieCountInDBModeData.observe(viewLifecycleOwner, Observer<Int> {
            binding.textViewMovieCount.text = it.toString()
        })
    }
}