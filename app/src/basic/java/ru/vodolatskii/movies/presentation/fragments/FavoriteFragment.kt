package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vodolatskii.movies.common.CheckTrialHelper
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.databinding.FragmentFavoriteTrialBinding


class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var bindingTrial: FragmentFavoriteTrialBinding
    private lateinit var trialHelper: CheckTrialHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        trialHelper = CheckTrialHelper(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        bindingTrial = FragmentFavoriteTrialBinding.inflate(inflater, container, false)
        return if (trialHelper.isTrialExpired()) bindingTrial.root else binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}