package presentation.viewModel

import data.LiveData
import data.RenderServiceConfig
import domain.RectDrawer
import domain.RenderServiceImpl
import domain.TransformerImpl
import org.w3c.dom.CanvasRenderingContext2D
import presentation.MainContract
import presentation.interactor.MainInteractor
import presentation.MainContract.Interactor.GameState

class MainViewModel(private val size: Int,
                    private val cellWidth: Double,
                    private val cellHeight: Double,
                    private val cellBorder: Double,
                    private val context: CanvasRenderingContext2D) : MainContract.ViewModel {

    private lateinit var interactor: MainContract.Interactor

    override val scoreObservable = LiveData<Int>()
    override val actionObservable = LiveData<MainContract.Action>()
    override val loseObservable = LiveData(false)

    init {
        initDependency()

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

        interactor.gameStateObservable.observe {
            when (it) {
                GameState.LOSE -> loseObservable.setValue(true)
                else -> {
                }
            }
        }

        interactor.startGame()
    }

    override fun onResize(size: Int,
                          cellWidth: Double,
                          cellHeight: Double,
                          cellBorder: Double,
                          context: CanvasRenderingContext2D) {
        interactor.updateConfig(RenderServiceConfig(size, cellWidth, cellHeight, cellBorder, context))
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

    private fun initDependency() {
        val drawer = RectDrawer(cellWidth, cellHeight)
        val transformer = TransformerImpl(size)
        val renderService = RenderServiceImpl(RenderServiceConfig(size, cellWidth, cellHeight, cellBorder, context),
                drawer)

        interactor = MainInteractor(renderService, transformer)
    }
}