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
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentAdapter
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentItemTouchHelperCallback
import ru.vodolatskii.movies.presentation.utils.contentRV.ContentRVItemDecoration


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }

    private fun initFavoriteRV(){
        binding.favoritesRecyclerView.apply {
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

            val callback = ContentItemTouchHelperCallback( this)
            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(this)

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(this)

//        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(this)

        }
    }







    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
            }
    }
}