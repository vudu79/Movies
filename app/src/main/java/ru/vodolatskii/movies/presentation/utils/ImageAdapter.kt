package ru.vodolatskii.movies.presentation.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc

class ImageAdapter(
    private val docs: List<Doc> = emptyList(),
    private val errorUrls: List<String> = emptyList(),
    private val onClick: (Doc) -> Unit
) :
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
        if (docs.isNotEmpty()) {
            val doc = docs[position]
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
        } else {
            val imageUrl = errorUrls[position]
            try {
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.imageView)
            } catch (e: Exception) {
                Log.d("mytag", "исключение из глайда - $e")
            }
        }
    }

    override fun getItemCount(): Int {
        if (docs.isNotEmpty()) {
            return docs.size
        } else return errorUrls.size
    }
}
