package g3.viewchoosephoto

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun <T> MutableLiveData<T>.notifyObserverPostValue() {
    this.postValue(this.value)
}