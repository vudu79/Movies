package ru.vodolatskii.movies

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.vodolatskii.movies.databinding.ActivityLaunchBinding
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.MainActivity

class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val tvAnimation = AnimationUtils.loadAnimation(this, R.anim.tv_set_anim)
        binding.tv.startAnimation(tvAnimation)
        binding.tv.setOnClickListener{
            binding.tv.animate()
                .setDuration(1000)
                .rotation(720f)
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .withEndAction {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
        }
    }
}