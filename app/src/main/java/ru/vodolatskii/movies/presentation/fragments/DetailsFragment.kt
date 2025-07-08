package ru.vodolatskii.movies.presentation.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.transition.AutoTransition
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.FragmentDetailsBinding
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.MainActivity
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    lateinit var viewModel: MoviesViewModel
    private lateinit var movie: Movie
    private val scope = CoroutineScope(Dispatchers.IO)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movie = arguments?.get("movie") as Movie

        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = AutoTransition().apply {
            enterTransition = transition
            duration = 500
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = (activity as MainActivity).shareMoviesViewModel()
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initContent(movie)

        setListeners(movie)

    }

    private fun initContent(movie: Movie) {
        (activity as MainActivity).findViewById<AppBarLayout>(R.id.topAppBarLayout).visibility =
            View.GONE

        binding.detailsToolbar.title = movie.title
        Glide.with(this)
            .load(movie.posterUrl)
            .centerCrop()
            .into(binding.detailsPoster)
        if (movie.isFavorite) {
            binding.detailsToolbar.menu.findItem(R.id.button_favorite_details)
                .setIcon(R.drawable.baseline_favorite_24)
        } else {
            binding.detailsToolbar.menu.findItem(R.id.button_favorite_details)
                .setIcon(R.drawable.baseline_favorite_border_24)
        }
        binding.detailsDescription.text = setTitleStyle(movie)
    }


    private fun setTitleStyle(movie: Movie): SpannableStringBuilder {
        val title = movie.title
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
            val title = movie.title
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
                        "Оложен ${movie.title} ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "Убрать"
                        ) {
                        }
                        .show()
                }

                R.id.button_favorite_details -> {
                    if (movie.isFavorite) {
                        binding.detailsToolbar.menu.findItem(R.id.button_favorite_details)
                            .setIcon(R.drawable.baseline_favorite_border_24)
                        movie.isFavorite = false
                        viewModel.deleteMovieFromFavorite(movie)
                        Snackbar.make(
                            binding.detailsDescription,
                            "Удален из избранного ${movie.title} ",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    } else {
                        binding.detailsToolbar.menu.findItem(R.id.button_favorite_details)
                            .setIcon(R.drawable.baseline_favorite_24)
                        movie.isFavorite = true
                        viewModel.addMovieToFavorite(movie)
                        Snackbar.make(
                            binding.detailsDescription,
                            "В избранном ${movie.title} ",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
            }
            false
        }

        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
            Toast.makeText(requireContext(),"jhgjhgjh", Toast.LENGTH_SHORT).show()
        }

        binding.detailsFab.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${movie.title} \n\n ${movie.description}"
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${movie.posterUrl}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        //Проверяем версию системы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Создаем объект для передачи данных
            val contentValues = ContentValues().apply {
                //Составляем информацию для файла (имя, тип, дата создания, куда сохранять и т.д.)
                put(MediaStore.Images.Media.TITLE, movie.title.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    movie.title.handleSingleQuote()
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000
                )
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            //Получаем ссылку на объект Content resolver, который помогает передавать информацию из приложения вовне
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            //Открываем канал для записи на диск
            val outputStream = contentResolver.openOutputStream(uri!!)!!
            //Передаем нашу картинку, может сделать компрессию
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            //Закрываем поток
            outputStream.close()
        } else {
            //То же, но для более старых версий ОС
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                movie.title.handleSingleQuote(),
                movie.description.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster() {
        Log.d("mytag", "gggg -- $${checkPermission()}")

        //Проверяем есть ли разрешение
        if (!checkPermission()) {

            //Если нет, то запрашиваем и выходим из метода
            requestPermission()
            return
        }
        //Создаем родительский скоуп с диспатчером Main потока, так как будем взаимодействовать с UI
        MainScope().launch {
            //Включаем Прогресс-бар
            binding.progressBar.isVisible = true
            //Создаем через async, так как нам нужен результат от работы, то есть Bitmap
            val job = scope.async {
                viewModel.loadWallpaper( movie.posterUrl)
            }
            //Сохраняем в галерею, как только файл загрузится
            saveToGallery(job.await())
            //Выводим снекбар с кнопкой перейти в галерею
            Snackbar.make(
                binding.root,
                R.string.downloaded_to_gallery,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = "image/*"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .show()

            //Отключаем Прогресс-бар
            binding.progressBar.isVisible = false
        }
    }

}