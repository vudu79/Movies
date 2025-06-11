package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vodolatskii.movies.databinding.FragmentSelectionsBinding
import ru.vodolatskii.movies.presentation.utils.AnimationHelper

class SelectionsFragment : Fragment() {
    private lateinit var binding: FragmentSelectionsBinding


//    init {
//        exitTransition = Fade(Fade.MODE_OUT).apply { duration = 500 }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(view, requireActivity(), 3)

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