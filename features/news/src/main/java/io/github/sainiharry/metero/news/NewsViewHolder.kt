package io.github.sainiharry.metero.news

import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.metero.news.databinding.ItemNewsBinding

internal class NewsViewHolder(
    private val itemNewsBinding: ItemNewsBinding,
    private val onItemClickListener: ItemClickListener<News>
) : RecyclerView.ViewHolder(itemNewsBinding.root) {

    private var news: News? = null

    init {
        itemNewsBinding.root.setOnClickListener {
            news?.let {
                onItemClickListener.onItemClicked(it, adapterPosition)
            }
        }
    }

    fun bind(news: News) {
        this@NewsViewHolder.news = news
        itemNewsBinding.news = news
        itemNewsBinding.newsIcon.load(news.urlToImage) {
            crossfade(true)
        }
        itemNewsBinding.executePendingBindings()
    }
}