package presentation

import data.MutableLiveData
import data.RenderServiceConfig
import data.*
import org.w3c.dom.CanvasRenderingContext2D

interface MainContract {
    /**
     * Enum for actions.
     */
    enum class Action {
        UP, DOWN, LEFT, RIGHT
    }

    interface Interactor {
        /**
         * Enum for gameState.
         */
        enum class GameState {
            STARTING, WIN, LOSE, INFINITE
        }

        /**
         * Observable for gameState.
         */
        val gameStateObservable: ImmutableLiveData<GameState>

        /**
         * Observable for score.
         */
        val scoreObservable: ImmutableLiveData<Int>

        /**
         * Method for start game.
         */
        fun startGame()

        /**
         * Method for handle action.
         *
         * @param action
         */
        fun actionMove(action: Action)

        /**
         * Method for redraw game pole.
         */
        fun redraw()

        /**
         * Method for updateConfig in {@link RenderServiceContract.RenderService}
         */
        fun updateConfig(config: RenderServiceConfig)

        /**
         * Method for update game state after win.
         */
        fun winHolderClick()
    }

    interface ViewModel {
        /**
         * Observable for lose state.
         */
        val loseObservable: ImmutableLiveData<Boolean>

        /**
         * Observable for action on view.
         */
        val actionObservable: MutableLiveData<Action>

        /**
         * Observable for score.
         */
        val scoreObservable: ImmutableLiveData<Int>

        /**
         * Observable for win state.
         */
        val winObservable: ImmutableLiveData<Boolean>

        /**
         * Method for resizing game pole.
         *
         * @param size pole size.
         * @param cellWidth cell's width in px.
         * @param cellHeight cell's height in px.
         * @param cellBorder space between cells.
         * @param context root context.
         */
        fun onResize(size: Int,
                     cellWidth: Double,
                     cellHeight: Double,
                     cellBorder: Double,
                     context: CanvasRenderingContext2D)

        /**
         * Method for restart game.
         */
        fun reload()

        /**
         * Method for cancel move.
         */
        fun undo()

        /**
         * Method for handle tap on winHolder.
         */
        fun onWinHolderClick()
    }
}