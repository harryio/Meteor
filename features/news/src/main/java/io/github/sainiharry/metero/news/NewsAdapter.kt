package io.github.sainiharry.metero.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.commonfeature.MeteorDiffUtil
import io.github.sainiharry.metero.news.databinding.ItemNewsBinding

internal class NewsAdapter(private val itemClickListener: ItemClickListener<News>) :
    ListAdapter<News, NewsViewHolder>(MeteorDiffUtil<News>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(getItem(position))
}