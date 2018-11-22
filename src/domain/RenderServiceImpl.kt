package domain

import data.*
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasTextAlign
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor
import presentation.clear
import kotlin.browser.window
import domain.RenderServiceContract.Transformer.ActionMove
import presentation.increment

object RenderServiceImpl : RenderServiceContract.RenderService, RenderServiceContract.ObservableProvider {

    private lateinit var config: RenderServiceConfig
    private lateinit var transformer: RenderServiceContract.Transformer

    private var cellList = mutableListOf<MutableList<Cell>>()
    override val scoreObservable = LiveData(0)
    override val lastStateObservable = LiveData<CacheModel>()
    override val changeListObservable = LiveData<List<List<Cell>>>()
    private var requestAnimationFrameValue: Int? = null

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

    override fun restoreState(cacheModel: CacheModel) {
        scoreObservable.setValue(cacheModel.score)
        cellList = cacheModel.cellList
    }

    override fun setRenderConfig(config: RenderServiceConfig) {
        this.config = config
        transformer = TransformerImpl(config.size)

        transformer.scoreChangedObservable.observe {
            scoreObservable.increment(it)
        }
    }

    private fun reset() {
        scoreObservable.setValue(0)
        config.context.apply {
            clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
        }
        cellList.clear()
    }

    private fun drawAllCells() {
        cellList.forEach {
            it.forEach { cell -> drawCell(cell) }
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
    override fun moveLeft() {
        lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.moveLeft(cellList)
        moveSideEffect(actionMoveList)
    }

    override fun moveUp() {
        lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.moveUp(cellList)
        moveSideEffect(actionMoveList)
    }

    override fun moveDown() {
        lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.moveDown(cellList)
        moveSideEffect(actionMoveList)
    }

    override fun moveRight() {
        lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
        val actionMoveList = transformer.moveRight(cellList)
        moveSideEffect(actionMoveList)
    }
    //endregion Transformer

    private fun calculFreeCell(): Int {
        var freeCell = 0
        cellList.forEach {
            it.forEach {
                freeCell += if (it.value == 0) 1 else 0
            }
        }
        return freeCell
    }

    private fun shallowCopyCellList(): MutableList<MutableList<Cell>> {
        return cellList.map { it.map { Cell(it.x, it.y, it.value) }.toMutableList() }.toMutableList()
    }

    private fun drawCell(cell: Cell) {
        with(config) {
            context.beginPath()

            context.rect(cell.x.toDouble(),
                    cell.y.toDouble(),
                    cellWidth.toDouble(),
                    cellHeight.toDouble())

            when (cell.value) {
                0 -> context.fillStyle = "#F3F35F"
                2 -> context.fillStyle = "#488281"
                4 -> context.fillStyle = "#C46B3E"
                8 -> context.fillStyle = "#FF4949"
                16 -> context.fillStyle = "#9966CC"
                32 -> context.fillStyle = "#5FACF3"
                64 -> context.fillStyle = "#8DB600"
                128 -> context.fillStyle = "#7BA05B"
                256 -> context.fillStyle = "#00D3B5"
                512 -> context.fillStyle = "#7FFFD4"
                1024 -> context.fillStyle = "#4B5320"
                2048 -> context.fillStyle = "#3B444B"
                4096 -> context.fillStyle = "#0000FF"
                else -> context.fillStyle = "#007FFF"
            }

            context.fill()

            if (cell.value != 0) {
                val fontSize = cellWidth / 2f
                context.font = "${fontSize}px Arial"
                context.fillStyle = "white"
                context.textAlign = CanvasTextAlign.CENTER
                context.fillText(cell.value.toString(), (cell.x + cellWidth / 2).toDouble(), (cell.y + cellWidth / 2).toDouble())
            }
        }
    }

    private fun createEmptyCell(row: Int, coll: Int): Cell {
        val x = coll * config.cellWidth.toDouble() + 5 * (coll + 1)
        val y = row * config.cellHeight.toDouble() + 5 * (row + 1)
        return Cell(x, y, 0)
    }

    private fun moveSideEffect(actionMoveList: List<ActionMove>) {
        val freeCellValue = calculFreeCell()

        val idling = checkIdle(actionMoveList)

        if (freeCellValue != 0 && !idling) {
            pasteNewCell()
            changeListObservable.setValue(cellList)
        }
    }

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