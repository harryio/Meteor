package io.github.sainiharry.meteor.commonfeature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    val loading: LiveData<Event<Boolean>>
        get() = _loading

    protected val _loading = MutableLiveData<Event<Boolean>>()

    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}