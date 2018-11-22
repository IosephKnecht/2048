package presentation

import data.MutableLiveData
import data.RenderServiceConfig
import data.*
import org.w3c.dom.CanvasRenderingContext2D

interface MainContract {
    enum class Action {
        UP, DOWN, LEFT, RIGHT
    }

    enum class State {
        IDLE, INIT
    }

    interface Interactor {
        fun startGame()
        fun actionMove(action: Action)
        fun resize(config: RenderServiceConfig)
        fun hasMoreMove(list: List<List<Cell>>): Boolean
        fun redraw()
    }

    interface ViewModel {
        val loseObservable: ImmutableLiveData<Boolean>
        val actionObservable: MutableLiveData<Action>
        val scoreObservable: ImmutableLiveData<Int>
        var state: State

        fun onResize(size: Int,
                     cellWidth: Double,
                     cellHeight: Double,
                     cellBorder: Double,
                     context: CanvasRenderingContext2D)
        fun reload()

        fun undo()
    }
}