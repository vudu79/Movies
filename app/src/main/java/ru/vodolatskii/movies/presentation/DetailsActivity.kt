package ru.vodolatskii.movies.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val doc = intent.extras?.get("doc") as Doc

        binding.detailsToolbar.title = doc.name
        binding.detailsToolbar.setTitleTextColor(R.color.white)
        Glide.with(this)
            .load(doc.poster.url)
            .centerCrop()
            .into(binding.detailsPoster)
        binding.detailsDescription.text = doc.description

    }
}