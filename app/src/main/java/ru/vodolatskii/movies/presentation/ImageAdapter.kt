package ru.vodolatskii.movies.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc

class ImageAdapter(private val imageUrls: List<Doc>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item) // Assuming you have an ImageView in your item layout with id 'imageView'
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_view_item, parent, false) // Replace item_image with your item layout
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position].poster.url


       try {
           Glide.with(holder.itemView.context)
               .load(imageUrl)
               .centerCrop()
               .into(holder.imageView)
       } catch (e: Exception){
           Log.d("mytag", "исключение из глайда - $e")
       }

    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}

// 4. Activity or Fragment
// Initialize RecyclerView and set adapter
/*
val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

val imageUrls = listOf(
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg",
    "https://example.com/image3.jpg"
)

val adapter = ImageAdapter(imageUrls)
recyclerView.adapter = adapter
*/