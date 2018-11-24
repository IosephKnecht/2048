package presentation.viewModel

import data.LiveData
import data.RenderServiceConfig
import domain.RectDrawer
import domain.RenderServiceImpl
import domain.TransformerImpl
import org.w3c.dom.CanvasRenderingContext2D
import presentation.MainContract
import presentation.MainContract.Interactor.GameState
import presentation.interactor.MainInteractor

class MainViewModel(private val size: Int,
                    private val cellWidth: Double,
                    private val cellHeight: Double,
                    private val cellBorder: Double,
                    private val context: CanvasRenderingContext2D) : MainContract.ViewModel {

    private lateinit var interactor: MainContract.Interactor

    override val scoreObservable = LiveData<Int>()
    override val actionObservable = LiveData<MainContract.Action>()
    override val loseObservable = LiveData(false)
    override val winObservable = LiveData(false)

    init {
        initDependency()

        actionObservable.observe {
            interactor.actionMove(it)
        }

        interactor.scoreObservable.observe {
            scoreObservable.setValue(it)
        }

        interactor.gameStateObservable.observe {
            when (it) {
                GameState.LOSE -> loseObservable.setValue(true)
                GameState.WIN -> winObservable.setValue(true)
                GameState.INFINITE -> winObservable.setValue(false)
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

    override fun onWinHolderClick() {
        interactor.winHolderClick()
    }

    override fun reload() {
        interactor.startGame()
        reset()
    }

    private fun reset() {
        scoreObservable.setValue(0)
        loseObservable.setValue(false)
    }

    // TODO: Bad solve for DI.
    private fun initDependency() {
        val drawer = RectDrawer(cellWidth, cellHeight)
        val transformer = TransformerImpl(size)
        val renderService = RenderServiceImpl(RenderServiceConfig(size, cellWidth, cellHeight, cellBorder, context),
                drawer)

        interactor = MainInteractor(renderService, transformer)
    }
}