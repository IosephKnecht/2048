package data

typealias Observer<T> = (T) -> Unit

interface MutableLiveData<T> {
    fun setValue(value: T)
}

interface ImmutableLiveData<T> {
    fun getValue(): T?
    fun observe(observer: Observer<T>)
    fun unsubscribe()
}

class LiveData<T>(private var value: T? = null) : ImmutableLiveData<T>, MutableLiveData<T> {
    private val observerList = mutableListOf<Observer<T>>()

    override fun setValue(value: T) {
        this.value = value
        observerList.forEach { it.invoke(value) }
    }

    override fun getValue(): T? {
        return value
    }

    override fun observe(observer: Observer<T>) {
        observerList.add(observer)
    }

    override fun unsubscribe() {
        observerList.clear()
    }
}