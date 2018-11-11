package presentation.interactor

import data.LiveData
import domain.RenderService
import presentation.MainContract

class MainInteractor(private val renderService: RenderService) : MainContract.Interactor {

    override val freeCellObservable = LiveData(true)

    init {
        renderService.freeCellObservable.observe {
            if (it == 0) freeCellObservable.setValue(false)
        }
    }

    override fun startGame() {
        renderService.createCells()
        renderService.drawAllCells()
        renderService.pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action): Int {
        return when (action) {
            MainContract.Action.DOWN -> renderService.moveDown()
            MainContract.Action.RIGHT -> renderService.moveRight()
            MainContract.Action.LEFT -> renderService.moveLeft()
            MainContract.Action.UP -> renderService.moveUp()
        }
    }
}