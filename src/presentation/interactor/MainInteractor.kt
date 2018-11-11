package presentation.interactor

import data.LiveData
import data.RenderServiceConfig
import domain.RenderService
import presentation.MainContract

class MainInteractor : MainContract.Interactor {

    override fun startGame() {
        RenderService.createCells()
        RenderService.drawAllCells()
        RenderService.pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action): Int {
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
}