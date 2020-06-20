package io.github.sainiharry.meteor.search

import androidx.recyclerview.widget.RecyclerView
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.model.Search
import io.github.sainiharry.meteor.search.databinding.ItemSearchBinding

internal class SearchViewHolder(
    private val itemSearchBinding: ItemSearchBinding,
    private val itemClickListener: ItemClickListener<Search>
) :
    RecyclerView.ViewHolder(itemSearchBinding.root) {

    private var search: Search? = null

    init {
        itemSearchBinding.root.setOnClickListener {
            search?.let {
                itemClickListener.onItemClicked(it, adapterPosition)
            }
        }
    }

    fun bind(query: Search) {
        this@SearchViewHolder.search = query
        itemSearchBinding.search = query
        itemSearchBinding.executePendingBindings()
    }
}