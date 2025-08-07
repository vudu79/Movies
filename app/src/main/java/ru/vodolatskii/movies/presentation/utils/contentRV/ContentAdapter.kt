package ru.vodolatskii.movies.presentation.utils.contentRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
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
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.RatingDonutView
import java.util.Collections

class ContentAdapter(
    private val onLoadMorePage: () -> Unit,
    private val onItemClick: (Movie, View) -> Unit,
    private val onMoveToFavorite: (Movie) -> Unit,
    private val onDeleteFromFavorite: (Movie) -> Unit,
    private val onDeleteFromPopular: (Movie) -> Unit,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ContentItemTouchHelperListener {

    private val movies = mutableListOf<Movie>()
    private var hasMore = false
    private var nextPageSize = 0
    private var currentPage = 0
    private var totalPages = 0
    private var totalItems = 0


    companion object {
        private const val TYPE_MOVIE_CARD = 0
        private const val TYPE_LOAD_MORE_BUTTON = 1
    }

    private val diffUtilsCallback: DiffUtil.ItemCallback<Movie> =
        object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.apiId == newItem.apiId
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

    fun updateData(
        newMovies: List<Movie>,
        hasMore: Boolean,
        nextPageSize: Int,
        currentPage: Int,
        totalPages: Int,
        totalItems: Int,
    ) {
        this.hasMore = hasMore
        this.nextPageSize = nextPageSize
        this.currentPage = currentPage
        this.totalPages = totalPages
        this.totalItems = totalItems
        movies.clear()
        movies.addAll(newMovies)
        asyncListDiffer.submitList(movies)
    }

    fun getData(): List<Movie> {
        return asyncListDiffer.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_MOVIE_CARD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_movie_item_layout, parent, false)
                ContentViewHolder(view)
            }

            TYPE_LOAD_MORE_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.button_load_more, parent, false)
                LoadMoreViewHolder(view, onLoadMorePage)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }


    override fun getItemCount(): Int = movies.size + if (hasMore) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (position == movies.size) TYPE_LOAD_MORE_BUTTON else TYPE_MOVIE_CARD
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                val movie = asyncListDiffer.currentList[position]
                holder.bind(movie = movie, onItemClick = onItemClick)
            }

            is LoadMoreViewHolder -> {
                holder.bind(nextPageSize, currentPage, totalPages, totalItems)
            }

            else -> {
            }
        }
    }


    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =
            itemView.findViewById(R.id.poster_image)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val releaseDate: TextView = itemView.findViewById(R.id.release_date)
        val card: CardView = itemView.findViewById(R.id.card)
        val rating: RatingDonutView = itemView.findViewById(R.id.rating_donut)
        val genres: TextView = itemView.findViewById(R.id.genre_list)
//        val shineView: View = itemView.findViewById(R.id.shine)

        fun bind(movie: Movie, onItemClick: (Movie, View) -> Unit) {
            Glide.with(itemView.context)
                .load(movie.posterUrl)
                .error(R.drawable.baseline_error_outline_24)
                .placeholder(R.drawable.baseline_arrow_circle_down_24)
                .centerCrop()
                .override(200, 200)
                .into(imageView)
            title.text = movie.title
            description.text = movie.description
            rating.setProgress((movie.rating * 10).toInt())
            card.setOnClickListener {
                ViewCompat.setTransitionName(description, "text_transition_name")
                onItemClick(movie, description)
            }
            releaseDate.text = "Дата выхода: " + movie.releaseDate
            val genreString = movie.genreListString.toString().replace("[", "").replace("]", "")
            genres.text = "Жанры: $genreString"
//                val genreString = movie.genreList.toGenresString()
//                setAnimation(holder.shineView)
        }
    }


    inner class LoadMoreViewHolder(
        itemView: View,
        private val onLoadMore: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val loadMoreButton: Button = itemView.findViewById(R.id.loadMoreButton)
        private val pageInfo: TextView = itemView.findViewById(R.id.pageInfo)

        fun bind(nextPageSize: Int, currentPage: Int, totalPages: Int, totalItems: Int) {
            loadMoreButton.text = "Показать еще $nextPageSize элементов"
            pageInfo.text = "Страница $currentPage из $totalPages (всего $totalItems элементов)"
            loadMoreButton.setOnClickListener { onLoadMore() }
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
        if (movie.isFavorite) {
            onDeleteFromFavorite(movie)
        }
        onDeleteFromPopular(movie)
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