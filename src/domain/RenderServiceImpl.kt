package domain

import data.Cell
import data.LiveData
import data.RenderServiceConfig
import domain.RenderServiceContract.Transformer.ActionMove
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasTextAlign
import presentation.clear
import kotlin.browser.window
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor

class RenderServiceImpl(private var config: RenderServiceConfig,
                        private var transformer: TransformerImpl,
                        private val drawer: RenderServiceContract.Drawer<Cell>) : RenderServiceContract.RenderService,
        RenderServiceContract.ObservableProvider {

    private var cellList = mutableListOf<MutableList<Cell>>()
    private var requestAnimationFrameValue: Int? = null

    override val changeListObservable = LiveData<List<List<Cell>>>()

    //region RenderService
    override fun startRender() {
        reset()
        createCells()
        animate()
        pasteNewCell()
    }

    override fun stopRender() {
        window.cancelAnimationFrame(requestAnimationFrameValue!!)
    }

    override fun restartService() {
        requestAnimationFrameValue?.let { window.cancelAnimationFrame(it) }

        reset()

        animate()
    }

    @Deprecated("migrate in interactor")
    override fun restoreState(cachedCellList: List<List<Cell>>) {
        cellList = cachedCellList.map { it.toMutableList() }.toMutableList()
    }

    override fun setRenderConfig(config: RenderServiceConfig) {
        this.config = config
        //FIXME
        transformer.updateSize(config.size)
        (drawer as RectDrawer).updateParams(config.cellWidth,config.cellHeight)
    }

    private fun reset() {
        config.context.clear()
        cellList.clear()
    }

    private fun drawAllCells() {
        cellList.forEach {
            it.forEach { cell -> drawer.drawElement(config.context, cell) }
        }
    }

    private fun createCells() {
        for (i in 0..(config.size - 1)) {
            cellList.add(mutableListOf())
            for (j in 0..(config.size - 1)) {
                cellList[i].add(createEmptyCell(i, j))
            }
        }
    }

    @Deprecated("migrate in interactor")
    private fun pasteNewCell() {
        while (true) {
            val row = floor(Math.random() * config.size).toInt()
            val coll = floor(Math.random() * config.size).toInt()

            if (cellList[row][coll].value == 0) {
                cellList[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
                return
            }
        }
    }
    //endregion RenderService

    //region Transformer
    @Deprecated("migrate in interactor")
    override fun moveLeft() {
        //lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.left(cellList)
        moveSideEffect(actionMoveList)
    }

    @Deprecated("migrate in interactor")
    override fun moveUp() {
        //lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.up(cellList)
        moveSideEffect(actionMoveList)
    }

    @Deprecated("migrate in interactor")
    override fun moveDown() {
        //lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.down(cellList)
        moveSideEffect(actionMoveList)
    }

    @Deprecated("migrate in interactor")
    override fun moveRight() {
        //lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.right(cellList)
        moveSideEffect(actionMoveList)
    }
    //endregion Transformer

    @Deprecated("migrate in interactor")
    private fun calculFreeCell(): Int {
        var freeCell = 0
        cellList.forEach {
            it.forEach {
                freeCell += if (it.value == 0) 1 else 0
            }
        }
        return freeCell
    }

    override fun copyList(): List<List<Cell>> {
        return cellList.map { it.map { Cell(it.x, it.y, it.value) } }
    }

    private fun createEmptyCell(row: Int, coll: Int): Cell {
        val x = coll * config.cellWidth + config.cellBorder * (coll + 1)
        val y = row * config.cellHeight + config.cellBorder * (row + 1)
        return Cell(x, y, 0)
    }

    @Deprecated("migrate in inreractor")
    private fun moveSideEffect(actionMoveList: List<ActionMove>) {
        val freeCellValue = calculFreeCell()

        val idling = checkIdle(actionMoveList)

        if (freeCellValue != 0 && !idling) {
            pasteNewCell()
            changeListObservable.setValue(cellList)
        }
    }

    @Deprecated("migrate in interactor")
    private fun checkIdle(actionMoveList: List<ActionMove>): Boolean {
        actionMoveList.forEach {
            if (it != ActionMove.FAILED_MOVE) return false
        }
        return true
    }

    private fun animate() {
        requestAnimationFrameValue = window.requestAnimationFrame {
            animate()
        }
        config.context.clear()
        drawAllCells()
    }
}