package presentation

import data.LiveData
import data.MutableLiveData

interface MainContract {
    enum class Action {
        UP, DOWN, LEFT, RIGHT
    }

    enum class State {
        IDLE, INIT
    }

    interface Interactor {
        val freeCellObservable: LiveData<Boolean>
        fun startGame()
        fun actionMove(action: Action): Int
    }

    interface ViewModel {
        val loseObservable: MutableLiveData<Boolean>
        val actionObservable: MutableLiveData<Action>
        val scoreObservable: MutableLiveData<Int>
        var state: State
    }
}