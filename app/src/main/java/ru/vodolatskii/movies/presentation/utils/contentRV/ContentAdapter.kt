package ru.vodolatskii.movies.presentation.utils.contentRV

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc
import java.util.Collections

class ContentAdapter(
    private val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), ContentItemTouchHelperListener {

    private val diffUtilsCallback: DiffUtil.ItemCallback<Doc> =
        object : DiffUtil.ItemCallback<Doc>() {
            override fun areItemsTheSame(oldItem: Doc, newItem: Doc): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Doc, newItem: Doc): Boolean {
                return oldItem == newItem
            }
        }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtilsCallback)

    fun setData(docs: List<Doc>) {
        val list = docs.toMutableList()
        asyncListDiffer.submitList(list)
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
//                holder.card.setOnClickListener { onClick(asyncListDiffer.currentList[position]) }
                Glide.with(holder.itemView.context)
                    .load(asyncListDiffer.currentList[position].poster.url)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.imageView)
                holder.title.text = asyncListDiffer.currentList[position].name
                holder.description.text = asyncListDiffer.currentList[position].description
            }

            else -> {

            }
        }
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val tempList: MutableList<Doc> = asyncListDiffer.currentList.toMutableList()
        Collections.swap(tempList, fromPosition, toPosition)
        setData(tempList)
        return true
    }

    override fun onItemDismiss(position: Int) {
        val tempList: MutableList<Doc> = asyncListDiffer.currentList.toMutableList()
        tempList.removeAt(position)
        setData(tempList)
    }


    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =
            itemView.findViewById(R.id.poster_image)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val card: CardView = itemView.findViewById(R.id.card)


    }

    interface OnItemClickListener {
        fun click(doc: Doc)
    }
}