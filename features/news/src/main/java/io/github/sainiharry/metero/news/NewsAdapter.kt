package io.github.sainiharry.metero.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import io.github.sainiharry.meteor.commonfeature.MeteorDiffUtil
import io.github.sainiharry.meteor.common.News
import io.github.sainiharry.metero.news.databinding.ItemNewsBinding

internal class NewsAdapter : ListAdapter<News, NewsViewHolder>(MeteorDiffUtil<News>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(getItem(position))
}