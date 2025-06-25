package ru.vodolatskii.movies.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentSettingsBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    lateinit var viewModel: MoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        viewModel = (activity as MainActivity).shareMoviesViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> {
            when (it) {
                POPULAR_CATEGORY -> binding.radioGroupCategory.check(R.id.radio_popular)
                TOP_RATED_CATEGORY -> binding.radioGroupCategory.check(R.id.radio_top_rated)
                UPCOMING_CATEGORY -> binding.radioGroupCategory.check(R.id.radio_upcoming)
                NOW_PLAYING_CATEGORY -> binding.radioGroupCategory.check(R.id.radio_now_playing)
            }
        })

        viewModel.requestLanguageLifeData.observe(viewLifecycleOwner, Observer<String> {
            when (it) {
                REQUEST_LANG_EN -> binding.radioGroupLanguage.check(R.id.radio_lang_en)
                REQUEST_LANG_RU -> binding.radioGroupLanguage.check(R.id.radio_lang_ru)
            }
        })

        viewModel.contentSourceLiveData.observe(viewLifecycleOwner, Observer<String> {
            when (it) {
                SOURCE_INTERNET -> binding.radioGroupInternetStorageHome.check(R.id.radio_internet_source)
                SOURCE_STORAGE -> binding.radioGroupInternetStorageHome.check(R.id.radio_storage_source)
            }
        })
    }

    private fun setupListeners() {
        binding.radioGroupCategory.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_now_playing -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { lang, checkedId ->
            when (checkedId) {
                R.id.radio_lang_en -> viewModel.putRequestLanguage(REQUEST_LANG_EN)
                R.id.radio_lang_ru -> viewModel.putRequestLanguage(REQUEST_LANG_RU)
            }
        }

        binding.radioGroupInternetStorageHome.setOnCheckedChangeListener { lang, checkedId ->
            when (checkedId) {
                R.id.radio_internet_source -> viewModel.putSource(SOURCE_INTERNET)
                R.id.radio_storage_source -> viewModel.putSource(SOURCE_STORAGE)
            }
        }
    }


    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"

        private const val REQUEST_LANG_RU = "ru-RU"
        private const val REQUEST_LANG_EN = "en-US"

        private const val SOURCE_INTERNET  = "internet"
        private const val SOURCE_STORAGE = "storage"
    }
}