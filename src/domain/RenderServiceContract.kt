package domain

import data.*

interface RenderServiceContract {
    interface RenderService {
        fun startRender()
        fun stopRender()
        fun restartService()
        fun restoreState(cacheModel: CacheModel)
        fun setRenderConfig(config: RenderServiceConfig)

        fun moveLeft()
        fun moveRight()
        fun moveDown()
        fun moveUp()
    }

    interface Transformer {
        enum class ActionMove {
            EMPTY_MOVE, SUCCESS_MOVE, FAILED_MOVE
        }

        val scoreChangedObservable: ImmutableLiveData<Int>

        fun moveLeft(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun moveRight(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun moveUp(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
        fun moveDown(cellList: MutableList<MutableList<Cell>>): List<ActionMove>
    }

    interface ObservableProvider {
        val scoreObservable: ImmutableLiveData<Int>
        val lastStateObservable: ImmutableLiveData<CacheModel>
        val changeListObservable: ImmutableLiveData<List<List<Cell>>>
    }
}