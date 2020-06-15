package io.github.sainiharry.metero.news

import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.github.sainiharry.meteor.repositories.news.News
import io.github.sainiharry.metero.news.databinding.ItemNewsBinding

internal class NewsViewHolder(private val itemNewsBinding: ItemNewsBinding) :
    RecyclerView.ViewHolder(itemNewsBinding.root) {

    fun bind(news: News) {
        itemNewsBinding.news = news
        itemNewsBinding.newsIcon.load(news.urlToImage) {
            crossfade(true)
        }
        itemNewsBinding.executePendingBindings()
    }
}