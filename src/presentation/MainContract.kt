package presentation

import data.MutableLiveData
import data.RenderServiceConfig
import data.*
import org.w3c.dom.CanvasRenderingContext2D

interface MainContract {
    enum class Action {
        UP, DOWN, LEFT, RIGHT
    }

    interface Interactor {
        enum class GameState {
            STARTING, WIN, LOSE, INFINITE
        }

        val gameStateObservable: ImmutableLiveData<GameState>
        val scoreObservable: ImmutableLiveData<Int>

        fun startGame()
        fun actionMove(action: Action)
        fun redraw()
        fun updateConfig(config: RenderServiceConfig)
        fun winHolderClick()
    }

    interface ViewModel {
        val loseObservable: ImmutableLiveData<Boolean>
        val actionObservable: MutableLiveData<Action>
        val scoreObservable: ImmutableLiveData<Int>
        val winObservable: ImmutableLiveData<Boolean>

        fun onResize(size: Int,
                     cellWidth: Double,
                     cellHeight: Double,
                     cellBorder: Double,
                     context: CanvasRenderingContext2D)

        fun reload()

        fun undo()

        fun onWinHolderClick()
    }
}