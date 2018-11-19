package presentation.interactor

import data.CacheModel
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

    override fun redraw() {
        cacheModel?.let {
            RenderServiceImpl.restoreState(it)
            RenderServiceImpl.drawAllCells()
        }
    }
}