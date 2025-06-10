package ru.vodolatskii.movies.presentation.utils.contentRV

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import java.util.Collections

class ContentAdapter(
    private val onItemClick: (Movie,View ) -> Unit,
    private val onMoveToFavorite: (Movie) -> Unit,
    private val onDeleteFromFavorite: (Movie) -> Unit,
    private val onDeleteFromPopular: (Movie) -> Unit,

    ) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), ContentItemTouchHelperListener {

    private val diffUtilsCallback: DiffUtil.ItemCallback<Movie> =
        object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtilsCallback)

    fun setData(Movies: List<Movie>) {
        val list = Movies.toMutableList()
        asyncListDiffer.submitList(list)
    }

    fun getData(): List<Movie> {
        return asyncListDiffer.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_content_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                val Movie = asyncListDiffer.currentList[position]

                Glide.with(holder.itemView.context)
                    .load(Movie.posterUrl)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.imageView)

                holder.title.text = Movie.name

                holder.description.text = Movie.description

                holder.card.setOnClickListener {
                    ViewCompat.setTransitionName(holder.description, "text_transition_name")
                    onItemClick(Movie, holder.description)
                }
            }

            else -> {
            }
        }
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val tempList: MutableList<Movie> = asyncListDiffer.currentList.toMutableList()
        Collections.swap(tempList, fromPosition, toPosition)
        setData(tempList)
        return true
    }

    override fun onItemDismiss(position: Int) {
        val tempList: MutableList<Movie> = asyncListDiffer.currentList.toMutableList()
        val movie = tempList.removeAt(position)
        setData(tempList)

        onDeleteFromPopular(movie)

        if (movie.isFavorite) {
            onDeleteFromFavorite(movie)
        }
    }

    override fun onItemAdd(movie: Movie, position: Int) {
        val tempList: MutableList<Movie> = asyncListDiffer.currentList.toMutableList()
        tempList.add(position, movie)
        setData(tempList)
    }

    override fun onItemSwipedToRight(movie: Movie, position: Int) {
        onItemDismiss(position)
        onMoveToFavorite(movie)
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =
            itemView.findViewById(R.id.poster_image)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val card: CardView = itemView.findViewById(R.id.card)
    }
}