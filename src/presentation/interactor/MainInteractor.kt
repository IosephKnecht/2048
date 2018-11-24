package presentation.interactor

import data.*
import domain.CellListChecker
import domain.RenderServiceContract
import domain.RenderServiceImpl
import domain.TransformerImpl
import presentation.MainContract
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor
import presentation.MainContract.Interactor.GameState

class MainInteractor(private val renderService: RenderServiceImpl,
                     private val transformer: RenderServiceContract.Transformer) : MainContract.Interactor {

    override val scoreObservable = LiveData<Int>()
    override val gameStateObservable = LiveData(GameState.STARTING)
    private var cacheModel: CacheModel? = null

    override fun startGame() {
        scoreObservable.setValue(0)
        renderService.startRender()
        pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action) {
        //addRestoreState()

        val mutableCellList = renderService.copyList()
        val immutableCellList = renderService.copyList()

        when (action) {
            MainContract.Action.DOWN -> transformer.down(mutableCellList)
            MainContract.Action.RIGHT -> transformer.right(mutableCellList)
            MainContract.Action.LEFT -> transformer.left(mutableCellList)
            MainContract.Action.UP -> transformer.up(mutableCellList)
        }

        if (mutableCellList != immutableCellList) {
            renderService.updateList(mutableCellList)
            addRestoreState(immutableCellList)

            if (CellListChecker.isEmpty(mutableCellList)) {
                pasteNewCell()

                // FIXME: hard logic
                if (!CellListChecker.checkColl(mutableCellList) &&
                        !CellListChecker.checkRow(mutableCellList)) {
                    gameStateObservable.setValue(GameState.LOSE)
                }
            }
        }
    }

    override fun redraw() {
        cacheModel?.let {
            scoreObservable.setValue(it.score)
            renderService.updateList(cacheModel!!.cellList)
            //renderService.restoreState(it.cellList)
            cacheModel = null
        }
    }

    override fun updateConfig(config: RenderServiceConfig) {
        renderService.stopRender()
        renderService.setRenderConfig(config)
        // FIXME
        (transformer as TransformerImpl).updateSize(config.size)
        startGame()
    }

    private fun pasteNewCell() {
        val cellList = renderService.copyList()

        while (true) {
            val row = floor(Math.random() * cellList.size).toInt()
            val coll = floor(Math.random() * cellList.size).toInt()

            if (cellList[row][coll].value == 0) {
                cellList[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
                renderService.updateList(cellList)
                return
            }
        }
    }

    private fun addRestoreState(cacheList: List<List<Cell>>) {
        cacheModel = CacheModel(cacheList, scoreObservable.getValue() ?: 0)
    }


//    // FIXME
//    private fun calculScore(cellList: List<List<Cell>>): Int {
//        var score = 0
//        cellList.forEach {
//            it.forEach {
//                score += it.value
//            }
//        }
//        return score
//    }
}