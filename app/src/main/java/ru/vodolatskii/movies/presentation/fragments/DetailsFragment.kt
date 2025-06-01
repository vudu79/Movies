package ru.vodolatskii.movies.presentation.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.databinding.FragmentDetailsBinding
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.MoviesViewModel


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    lateinit var viewModel: MoviesViewModel

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movie = arguments?.get("movie") as Movie
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).getMoviesViewModel()
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initContent(movie)

        setListeners(movie)

        (activity as MainActivity).findViewById<AppBarLayout>(R.id.topAppBarLayout).visibility =
            View.GONE
    }

    private fun initContent(movie: Movie) {
        binding.detailsToolbar.title = movie.name
        Glide.with(this)
            .load(movie.posterUrl)
            .centerCrop()
            .into(binding.detailsPoster)
        binding.detailsDescription.text = setTitleStyle(movie)
    }


    private fun setTitleStyle(movie: Movie): SpannableStringBuilder {
        val title = movie.name
        val description = "\n\n" + movie.description

        val boldStrUnderlineSpannable = SpannableStringBuilder(title)

        boldStrUnderlineSpannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        boldStrUnderlineSpannable.setSpan(
            ForegroundColorSpan(requireContext().resources.getColor(R.color.gradient_end)),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        boldStrUnderlineSpannable.setSpan(
            RelativeSizeSpan(1.3f), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        boldStrUnderlineSpannable.append(description)

        return boldStrUnderlineSpannable
    }

    private fun setListeners(movie: Movie) {
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val title = movie.name
            if (verticalOffset == 0) {
                binding.toolbarLayout.title = ""
                (activity as MainActivity).window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            } else if (Math.abs(verticalOffset) >= binding.appBar.scrollBarSize) {
                binding.toolbarLayout.title = title
                binding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ToolBarCollStyle)
                (activity as MainActivity).window.statusBarColor =
                    ContextCompat.getColor(requireContext(), R.color.gradient_end)
                (activity as MainActivity).window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_VISIBLE
            }
        }

        binding.detailsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.button_after_details -> {
                    Snackbar.make(
                        binding.detailsDescription,
                        "Оложен ${movie.name} ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "Убрать"
                        ) {
                        }
                        .show()
                }

                R.id.button_favorite_details -> {
                    Snackbar.make(
                        binding.detailsDescription,
                        "В избранном ${movie.name} ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "Убрать"
                        ) {
                        }
                        .show()
                }
            }
            false
        }

        binding.detailsFab.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${movie.name} \n\n ${movie.description}"
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${movie.posterUrl}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }
}