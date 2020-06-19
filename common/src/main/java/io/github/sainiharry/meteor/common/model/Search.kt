package io.github.sainiharry.meteor.common.model

import io.github.sainiharry.meteor.common.UniqueId

data class Search(val id: Long, val searchQuery: String): UniqueId {
    override fun getUniqueId(): Long = id
}