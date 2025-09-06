package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vodolatskii.movies.databinding.FragmentFavoriteBinding
import ru.vodolatskii.movies.databinding.FragmentFavoriteTrialBinding
import ru.vodolatskii.movies.databinding.FragmentSelectionsBinding
import ru.vodolatskii.movies.databinding.FragmentSelectionsTrialBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

class SelectionsFragment : Fragment() {
    private lateinit var binding: FragmentSelectionsBinding
    private lateinit var bindingTrial: FragmentSelectionsTrialBinding
    private lateinit var viewModel: MoviesViewModel
    private var isTrialExpired: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectionsBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).shareMoviesViewModel()

        isTrialExpired = viewModel.trialSubject.value ?: false

        binding = FragmentSelectionsBinding.inflate(inflater, container, false)
        bindingTrial = FragmentSelectionsTrialBinding.inflate(inflater, container, false)

        return if (isTrialExpired) bindingTrial.root else binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 3)

        binding.textedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.buttonNext.isEnabled = s.isNotBlank()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}