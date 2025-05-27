package ru.vodolatskii.movies.presentation.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc

class ContentAdapter(private val docs: List<Doc>, private val onClick: (Doc) -> Unit) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =
            itemView.findViewById(R.id.poster_image)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val card: CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_content_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return docs.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.card.setOnClickListener { onClick(docs[position]) }
        Glide.with(holder.itemView.context)
            .load(docs[position].poster.url)
            .centerCrop()
            .override(200, 200)
            .into(holder.imageView)
        holder.title.text = docs[position].name
        holder.description.text = docs[position].description
    }
}