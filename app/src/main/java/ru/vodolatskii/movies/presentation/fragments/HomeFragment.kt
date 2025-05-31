package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.repository.RepositoryImpl
import ru.vodolatskii.movies.databinding.FragmentHomeBinding
import ru.vodolatskii.movies.presentation.KPViewModel
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MyViewModelFactory
import ru.vodolatskii.movies.presentation.utils.UIState
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    private lateinit var contentAdapter: ContentAdapter

    val viewModel: KPViewModel by lazy {
        val factory = MyViewModelFactory(RepositoryImpl())
        ViewModelProvider(this, factory).get(KPViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewModel.loadPosters()

        setPostersViewsVisibility(UIState.Loading)

        initContentRecyclerView()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UIState.Success -> {
                            val mutableDocsList = uiState.listDoc.toMutableList().shuffled()
                            setPostersViewsVisibility(uiState)
                            contentAdapter.setData(mutableDocsList)
                        }

                        is UIState.Error -> {
                            val mutableDocsList =
                                uiState.apiErrorUrlsList.toMutableList().shuffled()
                            setPostersViewsVisibility(uiState)
                            contentAdapter.setData(mutableDocsList)

                        }

                        is UIState.Loading -> {
                            setPostersViewsVisibility(uiState)
                        }
                    }
                }
            }
        }
    }

    private fun initContentRecyclerView() {
        binding.recyclerviewContent.apply {
            contentAdapter = ContentAdapter {
                (activity as MainActivity).launchDetailsFragment(it)
            }

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

    private fun setPostersViewsVisibility(state: UIState) {
        when (state) {
            is UIState.Success -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
            }

            is UIState.Error -> {
                binding.progressCircular.visibility = View.GONE
                binding.recyclerviewContent.visibility = View.VISIBLE
//                binding.buttonPostersError.visibility = View.VISIBLE
//                binding.textviewErrorMessage.visibility = View.VISIBLE
            }

            UIState.Loading -> {
                binding.progressCircular.visibility = View.VISIBLE
                binding.recyclerviewContent.visibility = View.GONE
//                binding.buttonPostersError.visibility = View.GONE
//                binding.textviewErrorMessage.visibility = View.GONE
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
}