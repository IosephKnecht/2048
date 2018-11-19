package presentation.interactor

import data.CacheModel
import data.Cell
import data.RenderServiceConfig
import domain.RenderServiceImpl
import presentation.MainContract

class MainInteractor : MainContract.Interactor {

    private var cacheModel: CacheModel? = null

    init {
        RenderServiceImpl.lastState.observe {
            cacheModel = it
        }
    }

    override fun startGame() {
        RenderServiceImpl.createCells()
        RenderServiceImpl.drawAllCells()
        RenderServiceImpl.pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action) {
        return when (action) {
            MainContract.Action.DOWN -> RenderServiceImpl.moveDown()
            MainContract.Action.RIGHT -> RenderServiceImpl.moveRight()
            MainContract.Action.LEFT -> RenderServiceImpl.moveLeft()
            MainContract.Action.UP -> RenderServiceImpl.moveUp()
        }
    }

    override fun resize(config: RenderServiceConfig) {
        RenderServiceImpl.reset()
        RenderServiceImpl.config = config
        startGame()
    }

    override fun hasMoreMove(list: List<List<Cell>>): Boolean {
        val rowResult = checkRow(list)
        val collResult = checkColl(list)
        return rowResult || collResult
    }

    override fun redraw() {
        cacheModel?.let {
            RenderServiceImpl.restoreState(it)
            RenderServiceImpl.drawAllCells()
        }
    }

    private fun checkRow(list: List<List<Cell>>): Boolean {
        val size = list.size - 1
        for (i in 0..size) {
            for (j in 0..(size - 1)) {
                val currentCell = list[i][j]
                val nextCell = list[i][j + 1]

                if (nextCell.value == 0 || currentCell.value == nextCell.value) return true
            }
        }
        return false
    }

    private fun checkColl(list: List<List<Cell>>): Boolean {
        val size = list.size - 1
        for (j in 0..(size - 1)) {
            for (i in 0..size) {
                val currentCell = list[j][i]
                val nextCell = list[j + 1][i]

                if (nextCell.value == 0 || currentCell.value == nextCell.value) return true
            }
        }
        return false
    }
}