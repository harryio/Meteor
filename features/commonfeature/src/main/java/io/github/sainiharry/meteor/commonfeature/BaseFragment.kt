package io.github.sainiharry.meteor.commonfeature

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {

    protected fun defaultErrorHandler(): EventObserver<Int> = EventObserver { errorResId ->
        view?.let { view ->
            Snackbar.make(view, errorResId, Snackbar.LENGTH_LONG).show()
        }
    }
}