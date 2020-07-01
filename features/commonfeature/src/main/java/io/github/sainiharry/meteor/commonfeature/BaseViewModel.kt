package io.github.sainiharry.meteor.commonfeature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    val loading: LiveData<Event<Boolean>>
        get() = _loading

    protected val _loading = MutableLiveData<Event<Boolean>>()

    val error: LiveData<Event<Int>>
        get() = _error

    protected val _error = MutableLiveData<Event<Int>>()

    protected fun handleError(throwable: Throwable, errorMsgId: Int = R.string.error_generic) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        _error.value = Event(errorMsgId)
    }
}