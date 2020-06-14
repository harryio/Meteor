package io.github.sainiharry.meteor.commonfeature

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View?.showKeyboard() {
    if (this == null) {
        return
    }

    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}