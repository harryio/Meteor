package io.github.sainiharry.meteor.commonfeature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    val loading: LiveData<Event<Boolean>>
        get() = _loading

    protected val _loading = MutableLiveData<Event<Boolean>>()

    val error: LiveData<Event<Int>>
        get() = _error

    protected val _error = MutableLiveData<Event<Int>>()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun getErrorHandler(errorMsgId: Int = R.string.error_generic): (Throwable) -> Unit =
        { throwable ->
            if (BuildConfig.DEBUG) {
                throwable.printStackTrace()
            }

            _error.value = Event(errorMsgId)
        }

    protected fun handleError(throwable: Throwable, errorMsgId: Int = R.string.error_generic) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        _error.value = Event(errorMsgId)
    }
}