package ru.vodolatskii.movies.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc
import ru.vodolatskii.movies.databinding.ActivityDetailsBinding


class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val doc = intent.extras?.get("doc") as Doc

        initContent(doc)

        setListeners(doc)
    }

    private fun initContent(doc :Doc){
        binding.detailsToolbar.title = doc.name
        Glide.with(this)
            .load(doc.poster.url)
            .centerCrop()
            .into(binding.detailsPoster)
        binding.detailsDescription.text = doc.description
    }

    private fun setListeners(doc:Doc){
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                binding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ToolBarExpStyle)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            } else if (Math.abs(verticalOffset) >= binding.appBar.scrollBarSize) {
                binding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ToolBarCollStyle)
                window.statusBarColor = ContextCompat.getColor(this, R.color.gradient_end)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        })

        binding.detailsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.button_after_details -> {
                    Snackbar.make(
                        binding.detailsDescription,
                        "Оложен ${doc.name} ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "Убрать",
                            View.OnClickListener {
                            })
                        .show()
                }

                R.id.button_favorite_details -> {
                    Snackbar.make(
                        binding.detailsDescription,
                        "В избранном ${doc.name} ",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "Убрать",
                            View.OnClickListener {
                            })
                        .show()
                }
            }
            false
        }

        binding.detailsFab.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, doc.name)
            sendIntent.putExtra(Intent.EXTRA_TEXT, doc.poster.url)
            sendIntent.setType("text/plain")
            startActivity(Intent.createChooser(sendIntent, "Поделиться"))
        }
    }
}