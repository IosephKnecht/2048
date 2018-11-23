package domain

import data.*

interface RenderServiceContract {
    interface RenderService {
        fun startRender()
        fun stopRender()
        fun restartService()
        fun restoreState(cachedCellList: List<List<Cell>>)
        fun setRenderConfig(config: RenderServiceConfig)
        fun copyList(): List<List<Cell>>

        fun moveLeft()
        fun moveRight()
        fun moveDown()
        fun moveUp()
    }

    interface Transformer {
        enum class ActionMove {
            EMPTY_MOVE, SUCCESS_MOVE, FAILED_MOVE
        }

        fun left(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun right(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun up(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun down(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
    }

    interface ObservableProvider {
        val changeListObservable: ImmutableLiveData<List<List<Cell>>>
    }
}