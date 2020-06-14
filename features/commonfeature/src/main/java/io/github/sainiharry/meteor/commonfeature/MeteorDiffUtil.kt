package io.github.sainiharry.meteor.commonfeature

import androidx.recyclerview.widget.DiffUtil
import io.github.sainiharry.meteor.common.UniqueId

class MeteorDiffUtil<T> : DiffUtil.ItemCallback<T>() where T : UniqueId, T: Any {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.getUniqueId() == newItem.getUniqueId()

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.equals(newItem)
    }
}