package presentation.viewModel

import data.LiveData
import data.RenderServiceConfig
import org.w3c.dom.CanvasRenderingContext2D
import presentation.MainContract
import presentation.interactor.MainInteractor

class MainViewModel(size: Int,
                    cellWidth: Double,
                    cellHeight: Double,
                    cellBorder: Double,
                    context: CanvasRenderingContext2D) : MainContract.ViewModel {

    private val interactor: MainContract.Interactor

    override val scoreObservable = LiveData<Int>()
    override val actionObservable = LiveData<MainContract.Action>()
    override val loseObservable = LiveData(false)

    override var state = MainContract.State.IDLE

    init {
        interactor = MainInteractor()

        interactor.updateConfig(RenderServiceConfig(size, cellWidth, cellHeight, cellBorder, context))

        actionObservable.observe {
            if (!loseObservable.getValue()!!) {
                interactor.actionMove(it)
            }
        }

//        RenderServiceImpl.changeListObservable.observe {
//            loseObservable.setValue(!interactor.hasMoreMove(it))
//        }

        interactor.scoreObservable.observe {
            scoreObservable.setValue(it)
        }

        interactor.startGame()
    }

    override fun onResize(size: Int, cellWidth: Double, cellHeight: Double, cellBorder: Double, context: CanvasRenderingContext2D) {
        interactor.resize(RenderServiceConfig(size, cellWidth, cellHeight, cellBorder, context))
    }

    override fun undo() {
        loseObservable.setValue(false)
        interactor.redraw()
    }

    override fun reload() {
        interactor.startGame()
        reset()
    }

    private fun reset() {
        scoreObservable.setValue(0)
        loseObservable.setValue(false)
    }
}