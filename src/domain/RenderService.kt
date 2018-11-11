package domain

import data.Cell
import data.LiveData
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import kotlin.js.Math
import kotlin.math.ceil
import kotlin.math.floor

class RenderService(private val size: Int,
                    private val cellWidth: Int,
                    private val cellHeight: Int,
                    private val cellBorder: Int,
                    private val context: CanvasRenderingContext2D) {

    private var cellList = mutableListOf(mutableListOf<Cell>())
    private var score = 0
    val freeCellObservable = LiveData<Int>()


    fun drawAllCells() {
        cellList.forEach {
            it.forEach { cell -> drawCell(cell) }
        }
    }

    fun createCells() {
        for (i in 0..(size - 1)) {
            cellList.add(mutableListOf())
            for (j in 0..(size - 1)) {
                cellList[i].add(createEmtyCell(i, j))
            }
        }
    }

    private fun createEmtyCell(row: Int, coll: Int): Cell {
        val x = coll * cellWidth + 5 * (coll + 1)
        val y = row * cellWidth + 5 * (row + 1)
        return Cell(x, y, 0)
    }

    private fun drawCell(cell: Cell) {
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
            val fontSize = cellWidth / 2
            context.font = "${fontSize}px Arial"
            context.fillStyle = "white"
            context.textAlign = CanvasTextAlign.CENTER
            context.fillText(cell.value.toString(), (cell.x + cellWidth / 2).toDouble(), (cell.y + cellWidth / 2).toDouble())
        }
    }

    fun pasteNewCell() {
        val freeCell = calculFreeCell()

        if(freeCell == 0) {
            freeCellObservable.setValue(0)
            return
        }

        while (true) {
            val row = floor(Math.random() * size).toInt()
            val coll = floor(Math.random() * size).toInt()

            if (cellList[row][coll].value == 0) {
                cellList[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
                drawAllCells()
                return
            }
        }
    }

    fun moveUp(): Int {
        for (j in 0..(size - 1)) {
            for (i in 1..(size - 1)) {
                if (cellList[i][j].value != 0) {
                    var row = i
                    while (row > 0) {
                        if (cellList[row - 1][j].value == 0) {
                            cellList[row - 1][j].value = cellList[row][j].value
                            cellList[row][j].value = 0
                            row--
                        } else if (cellList[row - 1][j].value == cellList[row][j].value) {
                            cellList[row - 1][j].value *= 2
                            score += cellList[row - 1][j].value
                            cellList[row][j].value = 0
                            break
                        } else {
                            break
                        }
                    }
                }
            }
        }
        pasteNewCell()
        return score
    }

    fun moveDown(): Int {
        for (j in 0..(size - 1)) {
            for (i in (size - 1) downTo 0) {
                if (cellList[i][j].value != 0) {
                    var row = i
                    while (row + 1 < size) {
                        if (cellList[row + 1][j].value == 0) {
                            cellList[row + 1][j].value = cellList[row][j].value
                            cellList[row][j].value = 0
                            row++
                        } else if (cellList[row + 1][j].value == cellList[row][j].value) {
                            cellList[row + 1][j].value *= 2
                            score += cellList[row + 1][j].value
                            cellList[row][j].value = 0
                            break
                        } else {
                            break
                        }
                    }
                }
            }
        }
        pasteNewCell()
        return score
    }

    fun moveLeft(): Int {
        for (i in 0..(size - 1)) {
            for (j in 1..(size - 1)) {
                if (cellList[i][j].value != 0) {
                    var coll = j
                    while (coll - 1 >= 0) {
                        if (cellList[i][coll - 1].value == 0) {
                            cellList[i][coll - 1].value = cellList[i][coll].value
                            cellList[i][coll].value = 0
                            coll--
                        } else if (cellList[i][coll].value == cellList[i][coll - 1].value) {
                            cellList[i][coll - 1].value *= 2
                            score += cellList[coll - 1][j].value
                            cellList[i][coll].value = 0
                            break
                        } else {
                            break
                        }
                    }
                }
            }
        }
        pasteNewCell()
        return score
    }

    fun moveRight(): Int {
        for (i in 0..(size - 1)) {
            for (j in (size - 1) downTo 0) {
                if (cellList[i][j].value != 0) {
                    var coll = j
                    while (coll + 1 < size) {
                        if (cellList[i][coll + 1].value == 0) {
                            cellList[i][coll + 1].value = cellList[i][coll].value
                            cellList[i][coll].value = 0
                            coll++
                        } else if (cellList[i][coll].value == cellList[i][coll + 1].value) {
                            cellList[i][coll + 1].value *= 2
                            score += cellList[coll + 1][j].value
                            cellList[i][coll].value = 0
                            break
                        } else {
                            break
                        }
                    }
                }
            }
        }
        pasteNewCell()
        return score
    }

    private fun calculFreeCell(): Int {
        var freeCell = 0
        cellList.forEach {
            it.forEach {
                freeCell++
            }
        }
        return freeCell
    }
}