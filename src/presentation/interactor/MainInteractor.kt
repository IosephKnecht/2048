package presentation.interactor

import data.CacheModel
import data.Cell
import data.LiveData
import data.RenderServiceConfig
import domain.CellListChecker
import domain.RenderServiceContract
import domain.TransformerImpl
import presentation.MainContract
import presentation.MainContract.Interactor.GameState
import presentation.increment
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor

class MainInteractor(private val renderService: RenderServiceContract.RenderService<List<List<Cell>>>,
                     private val transformer: RenderServiceContract.Transformer<Cell, List<List<Cell>>>) : MainContract.Interactor {

    private val WIN_BORDER = 2048

    override val scoreObservable = LiveData<Int>()
    override val gameStateObservable = LiveData<GameState>()
    private var cacheModel: CacheModel? = null

    init {
        transformer.transformChangeObservable.observe {
            if (it is Int) {
                scoreObservable.increment(it)

                if (gameStateObservable.getValue() != GameState.INFINITE && it == WIN_BORDER)
                    gameStateObservable.setValue(GameState.WIN)
            }
        }
    }

    override fun startGame() {
        scoreObservable.setValue(0)
        gameStateObservable.setValue(GameState.STARTING)
        renderService.startRender()
        pasteNewCell()
    }

    override fun actionMove(action: MainContract.Action) {
        if (isReactMoving()) {
            val mutableCellList = renderService.copyList()
            val immutableCellList = renderService.copyList()

            when (action) {
                MainContract.Action.DOWN -> transformer.down(mutableCellList)
                MainContract.Action.RIGHT -> transformer.right(mutableCellList)
                MainContract.Action.LEFT -> transformer.left(mutableCellList)
                MainContract.Action.UP -> transformer.up(mutableCellList)
            }

            moveSideEffect(mutableCellList, immutableCellList)
        }
    }

    override fun winHolderClick() {
        gameStateObservable.setValue(GameState.INFINITE)
    }

    override fun redraw() {
        cacheModel?.let {
            scoreObservable.setValue(it.score)
            renderService.updateList(cacheModel!!.cellList)
            cacheModel = null
        }
    }

    override fun updateConfig(config: RenderServiceConfig) {
        gameStateObservable.setValue(GameState.STARTING)
        renderService.stopRender()
        renderService.setRenderConfig(config)
        // FIXME
        (transformer as TransformerImpl).updateSize(config.size)
        startGame()
    }

    private fun pasteNewCell() {
        val cellList = renderService.copyList()

        while (true) {
            val row = floor(Math.random() * cellList.size).toInt()
            val coll = floor(Math.random() * cellList.size).toInt()

            if (cellList[row][coll].value == 0) {
                cellList[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
                renderService.updateList(cellList)
                return
            }
        }
    }

    private fun addRestoreState(cacheList: List<List<Cell>>) {
        cacheModel = CacheModel(cacheList, scoreObservable.getValue() ?: 0)
    }

    private fun moveSideEffect(mutableCellList: List<List<Cell>>, immutableCellList: List<List<Cell>>) {
        if (!CellListChecker.checkColl(mutableCellList) &&
                !CellListChecker.checkRow(mutableCellList)) {
            gameStateObservable.setValue(GameState.LOSE)
        }

        if (mutableCellList != immutableCellList) {
            renderService.updateList(mutableCellList)
            addRestoreState(immutableCellList)

            if (CellListChecker.isEmpty(mutableCellList)) {
                pasteNewCell()
            }
        }
    }

    private fun isReactMoving(): Boolean {
        val gameStateValue = gameStateObservable.getValue()
        return gameStateValue != GameState.WIN && gameStateValue != GameState.LOSE
    }
}