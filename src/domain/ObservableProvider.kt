package domain

import data.CacheModel
import data.Cell
import data.ImmutableLiveData

interface ObservableProvider {
    val scoreObservable: ImmutableLiveData<Int>
    val lastStateObservable: ImmutableLiveData<CacheModel>
    val changeListObservable: ImmutableLiveData<List<List<Cell>>>
}