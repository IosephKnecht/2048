package presentation.interactor

import data.*
import domain.RenderServiceContract
import domain.RenderServiceImpl
import domain.TransformerImpl
import presentation.MainContract

class MainInteractor(private val renderService: RenderServiceImpl,
                     private val transformer: RenderServiceContract.Transformer) : MainContract.Interactor {

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
        //addRestoreState()

        val mutableCellList = renderService.copyList()
        val immutableCellList = renderService.copyList()

        when (action) {
            MainContract.Action.DOWN -> transformer.down(mutableCellList)
            MainContract.Action.RIGHT -> transformer.right(mutableCellList)
            MainContract.Action.LEFT -> transformer.left(mutableCellList)
            MainContract.Action.UP -> transformer.up(mutableCellList)
        }

        if (mutableCellList != immutableCellList) renderService.updateList(mutableCellList)
    }

    override fun redraw() {
        cacheModel?.let {
            scoreObservable.setValue(it.score)
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

    private fun addRestoreState() {
        val list = renderService.copyList()
        cacheModel = CacheModel(list, scoreObservable.getValue() ?: 0)
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