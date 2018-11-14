package presentation

import data.MutableLiveData
import data.RenderServiceConfig
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
        fun redraw()
    }

    interface ViewModel {
        val loseObservable: MutableLiveData<Boolean>
        val actionObservable: MutableLiveData<Action>
        val scoreObservable: MutableLiveData<Int>
        var state: State

        fun onResize(size: Int,
                     cellWidth: Int,
                     cellHeight: Int,
                     cellBorder: Int,
                     context: CanvasRenderingContext2D)
        fun reload()
        fun undo()
    }
}