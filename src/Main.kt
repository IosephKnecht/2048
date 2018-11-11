import org.w3c.dom.CENTER
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.js.Math
import kotlin.math.*

private var width = 0
private var size = 0
private val cells = mutableListOf(mutableListOf<Cell>())
private lateinit var context: CanvasRenderingContext2D

data class Cell(val row: Int,
                val coll: Int,
                var value: Int = 0)

fun main(args: Array<String>) {
    val canvas = document.getElementById("canvas")!! as HTMLCanvasElement
    context = canvas.getContext("2d")!! as CanvasRenderingContext2D

    val sizeInput = document.getElementById("size")!!
    val changeSize = document.getElementById("change_size")!!
    val scoreLabel = document.getElementById("score")!!

    var score = 0
    size = 4
    width = canvas.width / size - 6

    var lose = false

    startGame()

    document.onkeydown = {
        if (it is KeyboardEvent && !lose) {
            when {
                it.keyCode == 38 || it.keyCode == 87 -> moveUp()
                it.keyCode == 39 || it.keyCode == 68 -> moveRight()
                it.keyCode == 40 || it.keyCode == 83 -> moveDown()
                it.keyCode == 37 || it.keyCode == 65 -> moveLeft()
            }
            //FIXME: update score
        }
    }
}

fun startGame() {
    createCells()
    drawAllCells()
    pasteNewCell()
}

fun cell(row: Int, coll: Int): Cell {
    val value = 0
    val x = coll * width + 5 * (coll + 1)
    val y = row * width + 5 * (row + 1)
    return Cell(x, y, value)
}

fun createCells() {
    val end = size - 1

    for (i in 0..end) {
        cells.add(mutableListOf())
        for (j in 0..end) {
            cells[i].add(cell(i, j))
        }
    }
}

fun drawCell(cell: Cell) {
    context.beginPath()

    context.rect(cell.row.toDouble(),
            cell.coll.toDouble(),
            width.toDouble(),
            width.toDouble())

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
        val fontSize = width / 2
        context.font = "${fontSize}px Arial"
        context.fillStyle = "white"
        context.textAlign = CanvasTextAlign.CENTER
        context.fillText(cell.value.toString(), (cell.row + width / 2).toDouble(), (cell.coll + width / 2).toDouble())
    }
}

fun drawAllCells() {
    cells.forEach {
        it.forEach { cell -> drawCell(cell) }
    }
}

fun pasteNewCell() {
    while (true) {
        val row = floor(Math.random() * size).toInt()
        val coll = floor(Math.random() * size).toInt()

        if (cells[row][coll].value == 0) {
            cells[row][coll].value = 2 * ceil(Math.random() * 2).toInt()
            drawAllCells()
            return
        }
    }
}

fun moveUp() {
    for (j in 0..(size - 1)) {
        for (i in 1..(size - 1)) {
            if (cells[i][j].value != 0) {
                var row = i
                while (row > 0) {
                    if (cells[row - 1][j].value == 0) {
                        cells[row - 1][j].value = cells[row][j].value
                        cells[row][j].value = 0
                        row--
                    } else if (cells[row - 1][j].value == cells[row][j].value) {
                        cells[row - 1][j].value *= 2
                        //FIXME increment score
                        cells[row][j].value = 0
                        break
                    } else {
                        break
                    }
                }
            }
        }
    }
    pasteNewCell()
}

fun moveDown() {
    for (j in 0..(size - 1)) {
        for (i in (size - 1) downTo 0) {
            if (cells[i][j].value != 0) {
                var row = i
                while (row + 1 < size) {
                    if (cells[row + 1][j].value == 0) {
                        cells[row + 1][j].value = cells[row][j].value
                        cells[row][j].value = 0
                        row++
                    } else if (cells[row + 1][j].value == cells[row][j].value) {
                        cells[row + 1][j].value *= 2
                        //FIXME increment score
                        cells[row][j].value = 0
                        break
                    } else {
                        break
                    }
                }
            }
        }
    }
    pasteNewCell()
}

fun moveLeft() {
    for (i in 0..(size - 1)) {
        for (j in 1..(size - 1)) {
            if (cells[i][j].value != 0) {
                var coll = j
                while (coll - 1 >= 0) {
                    if (cells[i][coll - 1].value == 0) {
                        cells[i][coll - 1].value = cells[i][coll].value
                        cells[i][coll].value = 0
                        coll--
                    } else if (cells[i][coll].value == cells[i][coll - 1].value) {
                        cells[i][coll - 1].value *= 2
                        //FIXME increment score
                        cells[i][coll].value = 0
                        break
                    } else {
                        break
                    }
                }
            }
        }
    }
    pasteNewCell()
}

fun moveRight() {
    for (i in 0..(size - 1)) {
        for (j in (size - 1) downTo 0) {
            if (cells[i][j].value != 0) {
                var coll = j
                while (coll + 1 < size) {
                    if (cells[i][coll + 1].value == 0) {
                        cells[i][coll + 1].value = cells[i][coll].value
                        cells[i][coll].value = 0
                        coll++
                    } else if (cells[i][coll].value == cells[i][coll + 1].value) {
                        cells[i][coll + 1].value *= 2
                        //FIXME increment score
                        cells[i][coll].value = 0
                        break
                    } else {
                        break
                    }
                }
            }
        }
    }
    pasteNewCell()
}