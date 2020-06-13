package io.github.sainiharry.meteor.commonfeature

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}