package presentation.viewModel

import data.LiveData
import data.RendrerServiceConfig
import domain.RenderService
import org.w3c.dom.CanvasRenderingContext2D
import presentation.MainContract
import presentation.interactor.MainInteractor

class MainViewModel(size: Int,
                    cellWidth: Int,
                    cellHeight: Int,
                    cellBorder: Int,
                    context: CanvasRenderingContext2D) : MainContract.ViewModel {

    private val interactor: MainContract.Interactor

    override val scoreObservable = LiveData<Int>()
    override val actionObservable = LiveData<MainContract.Action>()
    override val loseObservable = LiveData(false)

    override var state = MainContract.State.IDLE

    init {
        interactor = MainInteractor()

        RenderService.config = RendrerServiceConfig(size, cellWidth, cellHeight, cellBorder, context)

        actionObservable.observe {
            if (!loseObservable.getValue()!!) {
                val score = interactor.actionMove(it)
                scoreObservable.setValue(score)
            }
        }

        interactor.freeCellObservable.observe {
            if (!it) loseObservable.setValue(true)
        }

        interactor.startGame()
    }


}