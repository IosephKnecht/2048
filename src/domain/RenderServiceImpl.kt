package domain

import data.*
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasTextAlign
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor

object RenderServiceImpl : RenderService, Transformer, ObservableProvider {

    lateinit var config: RenderServiceConfig
    private var cellList = mutableListOf<MutableList<Cell>>()
    override val scoreObservable = LiveData(0)
    override val lastStateObservable = LiveData<CacheModel>()
    override val changeListObservable = LiveData<List<List<Cell>>>()

    //region RenderService
    override fun reset() {
        scoreObservable.setValue(0)
        config.context.apply {
            clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
        }
        cellList.clear()
    }

    override fun restoreState(cacheModel: CacheModel) {
        scoreObservable.setValue(cacheModel.score)
        cellList = cacheModel.cellList
    }

    override fun drawAllCells() {
        cellList.forEach {
            it.forEach { cell -> drawCell(cell) }
        }
    }

    override fun createCells() {
        for (i in 0..(config.size - 1)) {
            cellList.add(mutableListOf())
            for (j in 0..(config.size - 1)) {
                cellList[i].add(createEmptyCell(i, j))
            }
        }
    }

    override fun pasteNewCell() {
        while (true) {
            val row = floor(Math.random() * config.size).toInt()
            val coll = floor(Math.random() * config.size).toInt()

            if (cellList[row][coll].value == 0) {
                cellList[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
                drawAllCells()
                return
            }
        }
    }
    //endregion RenderService

    //region Transformer
    override fun moveLeft() {
        moveUpLeftTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle - 1) >= 0 }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle - 1]
            val currentCell = cellList[externalCycle][innerCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveUpLeftTemplate true
            } else if (currentCell.value == shiftCell.value) {
                shiftCell.value *= 2
                scoreObservable.setValue(scoreObservable.getValue()!! + shiftCell.value)
                currentCell.value = 0
                return@moveUpLeftTemplate false
            } else {
                return@moveUpLeftTemplate false
            }
        }
    }

    override fun moveUp() {
        moveUpLeftTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle > 0) }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle - 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveUpLeftTemplate true
            } else if (shiftCell.value == currentCell.value) {
                shiftCell.value *= 2
                scoreObservable.setValue(scoreObservable.getValue()!! + shiftCell.value)
                currentCell.value = 0
                return@moveUpLeftTemplate false
            } else {
                return@moveUpLeftTemplate false
            }
        }
    }

    override fun moveDown() {
        moveDownRightTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle + 1) < config.size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle + 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveDownRightTemplate true
            } else if (shiftCell.value == currentCell.value) {
                shiftCell.value *= 2
                scoreObservable.setValue(scoreObservable.getValue()!! + shiftCell.value)
                currentCell.value = 0
                return@moveDownRightTemplate false
            } else {
                return@moveDownRightTemplate false
            }
        }
    }

    override fun moveRight() {
        moveDownRightTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle + 1) < config.size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle + 1]
            val currentCell = cellList[externalCycle][innerCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveDownRightTemplate true
            } else if (currentCell.value == shiftCell.value) {
                shiftCell.value *= 2
                scoreObservable.setValue(scoreObservable.getValue()!! + shiftCell.value)
                currentCell.value = 0
                return@moveDownRightTemplate false
            } else {
                return@moveDownRightTemplate false
            }
        }
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

    private fun moveUpLeftTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                   whilePredicate: (innerCycle: Int) -> Boolean,
                                   blockWhile: (innerCycle: Int, externalCycle: Int) -> Boolean) {
        for (i in 0..(config.size - 1)) {
            for (j in 1..(config.size - 1)) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        if (blockWhile.invoke(temp, i)) temp--
                        else break
                    }
                }
            }
        }

        val freCellValue = calculFreeCell()

        if (freCellValue != 0) {
            lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
            pasteNewCell()
        }

        changeListObservable.setValue(cellList)
    }

    private fun moveDownRightTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                      whilePredicate: (innerCycle: Int) -> Boolean,
                                      blockWhile: (innerCycle: Int, externalCycle: Int) -> Boolean) {
        for (i in 0..(config.size - 1)) {
            for (j in (config.size - 2) downTo 0) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        if (blockWhile.invoke(temp, i)) temp++
                        else break
                    }
                }
            }
        }

        val freCellValue = calculFreeCell()

        if (freCellValue != 0) {
            lastStateObservable.setValue(CacheModel(shallowCopyCellList(), scoreObservable.getValue()!!))
            pasteNewCell()
        }

        changeListObservable.setValue(cellList)
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
                0 -> context.fillStyle = "#FFD300"
                2 -> context.fillStyle = "#E52B50"
                4 -> context.fillStyle = "#FFBF00"
                8 -> context.fillStyle = "#FF033E"
                16 -> context.fillStyle = "#9966CC"
                32 -> context.fillStyle = "#F2F3F4"
                64 -> context.fillStyle = "#8DB600"
                128 -> context.fillStyle = "#7BA05B"
                256 -> context.fillStyle = "#00FFFF"
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
        val x = coll * config.cellWidth + 5 * (coll + 1)
        val y = row * config.cellHeight + 5 * (row + 1)
        return Cell(x, y, 0)
    }
}