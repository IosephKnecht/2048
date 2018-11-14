package presentation.interactor

import data.CacheModel
import data.RenderServiceConfig
import domain.RenderService
import presentation.MainContract

class MainInteractor : MainContract.Interactor {

    private var cacheModel: CacheModel? = null

    init {
        RenderService.lastState.observe {
            cacheModel = it
        }
    }

    override fun startGame() {
        RenderService.reset()
        RenderService.createCells()
        RenderService.drawAllCells()
        RenderService.pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action) {
        return when (action) {
            MainContract.Action.DOWN -> RenderService.moveDown()
            MainContract.Action.RIGHT -> RenderService.moveRight()
            MainContract.Action.LEFT -> RenderService.moveLeft()
            MainContract.Action.UP -> RenderService.moveUp()
        }
    }

    override fun resize(config: RenderServiceConfig) {
        RenderService.reset()
        RenderService.config = config
        startGame()
    }

    override fun redraw() {
        cacheModel?.let {
            RenderService.restoreState(it)
            RenderService.drawAllCells()
        }
    }
}