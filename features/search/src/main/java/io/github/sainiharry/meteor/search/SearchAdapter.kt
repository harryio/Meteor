package io.github.sainiharry.meteor.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.commonfeature.MeteorDiffUtil
import io.github.sainiharry.meteor.search.databinding.ItemSearchBinding
import io.github.sainiharry.searchrepository.Search

internal class SearchAdapter(private val itemClickListener: ItemClickListener<Search>) :
    ListAdapter<Search, SearchViewHolder>(MeteorDiffUtil<Search>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context)),
            itemClickListener
        )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) =
        holder.bind(getItem(position))
}