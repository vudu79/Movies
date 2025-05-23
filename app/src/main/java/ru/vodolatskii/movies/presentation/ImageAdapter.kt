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

class ImageAdapter(private val imageUrls: List<Doc>, private val onClick: (Doc) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =
            itemView.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.recycle_view_item,
                parent,
                false
            )
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val doc = imageUrls[position]
        val imageUrl = doc.poster.url

        holder.imageView.setOnClickListener { onClick(doc) }

        try {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView)
        } catch (e: Exception) {
            Log.d("mytag", "исключение из глайда - $e")
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}
