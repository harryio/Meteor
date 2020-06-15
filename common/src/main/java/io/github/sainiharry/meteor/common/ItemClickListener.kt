package io.github.sainiharry.meteor.common

interface ItemClickListener<T : Any> {

    fun onItemClicked(item: T, position: Int)
}