package presentation.interactor

import data.*
import domain.RenderServiceImpl
import presentation.MainContract

class MainInteractor(private val renderService: RenderServiceImpl) : MainContract.Interactor {

    override val scoreObservable = LiveData<Int>()

    private var cacheModel: CacheModel? = null

    init {
        renderService.changeListObservable.observe {
            scoreObservable.setValue(calculScore(it))
        }
    }

    override fun startGame() {
        scoreObservable.setValue(0)
        renderService.startRender()
    }

    override fun actionMove(action: MainContract.Action) {
        addRestoreState()
        return when (action) {
            MainContract.Action.DOWN -> renderService.moveDown()
            MainContract.Action.RIGHT -> renderService.moveRight()
            MainContract.Action.LEFT -> renderService.moveLeft()
            MainContract.Action.UP -> renderService.moveUp()
        }
    }

    override fun resize(config: RenderServiceConfig) {
        renderService.stopRender()
        renderService.setRenderConfig(config)
        startGame()
    }

    override fun hasMoreMove(list: List<List<Cell>>): Boolean {
        val rowResult = checkRow(list)
        val collResult = checkColl(list)
        return rowResult || collResult
    }

    override fun redraw() {
        cacheModel?.let {
            scoreObservable.setValue(it.score)
            renderService.restoreState(it.cellList)
            cacheModel = null
        }
    }

    override fun updateConfig(config: RenderServiceConfig) {
        renderService.setRenderConfig(config)
    }

    private fun addRestoreState() {
        val list = renderService.copyList()
        cacheModel = CacheModel(list, scoreObservable.getValue() ?: 0)
    }

    private fun checkRow(list: List<List<Cell>>): Boolean {
        val size = list.size - 1
        for (i in 0..size) {
            for (j in 0..(size - 1)) {
                val currentCell = list[i][j]
                val nextCell = list[i][j + 1]

                if (currentCell.value == 0 ||
                        nextCell.value == 0 ||
                        currentCell.value == nextCell.value) return true
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

                if (currentCell.value == 0 ||
                        nextCell.value == 0 ||
                        currentCell.value == nextCell.value) return true
            }
        }
        return false
    }

    // FIXME
    private fun calculScore(cellList: List<List<Cell>>): Int {
        var score = 0
        cellList.forEach {
            it.forEach {
                score += it.value
            }
        }
        return score
    }
}