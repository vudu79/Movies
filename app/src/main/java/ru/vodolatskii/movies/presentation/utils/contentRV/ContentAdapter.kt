package ru.vodolatskii.movies.presentation.utils.contentRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.dto.toGenresString
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.RatingDonutView
import java.util.Collections

class ContentAdapter(
    private val onItemClick: (Movie, View) -> Unit,
    private val onMoveToFavorite: (Movie) -> Unit,
    private val onDeleteFromFavorite: (Movie) -> Unit,
    private val onDeleteFromPopular: (Movie) -> Unit,
    private val context: Context

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

    fun setData(movies: List<Movie>) {
        val list = movies.toMutableList()
        asyncListDiffer.submitList(list)
    }

    fun getData(): List<Movie> {
        return asyncListDiffer.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_movie_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                val movie = asyncListDiffer.currentList[position]

                Glide.with(holder.itemView.context)
                    .load(movie.posterUrl)
                    .error(R.drawable.baseline_error_outline_24)
                    .placeholder(R.drawable.baseline_arrow_circle_down_24)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.imageView)

                holder.title.text = movie.title

                holder.description.text = movie.description

                holder.rating.setProgress((movie.rating * 10).toInt())

                holder.card.setOnClickListener {
                    ViewCompat.setTransitionName(holder.description, "text_transition_name")
                    onItemClick(movie, holder.description)
                }
                holder.releaseDate.text = "Дата выхода: " + movie.releaseDate

                val genreString = movie.genreList.toGenresString()
                holder.genres.text = "Жанры: $genreString"
//                setAnimation(holder.shineView)
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
        val releaseDate: TextView = itemView.findViewById(R.id.release_date)
        val card: CardView = itemView.findViewById(R.id.card)
        val rating: RatingDonutView = itemView.findViewById(R.id.rating_donut)
        val genres: TextView = itemView.findViewById(R.id.genre_list)
//        val shineView: View = itemView.findViewById(R.id.shine)
    }

    private fun setAnimation(viewToAnimate: View) {
        try {
            val anim = AnimationUtils.loadAnimation(context, R.anim.left_right_shine_anim)
            viewToAnimate.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(p0: Animation) {
                    CoroutineScope(Dispatchers.Default).launch {
                        viewToAnimate.startAnimation(anim)
                    }
                }

                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationRepeat(p0: Animation?) {}
            })
        } catch (e: Exception) {

        }
    }
}