package domain

import data.Cell
import data.LiveData
import domain.RenderServiceContract.Transformer.ActionMove

class TransformerImpl(private var size: Int) : RenderServiceContract.Transformer {

    fun updateSize(size: Int) {
        this.size = size
    }

    override fun left(cellList: List<List<Cell>>): List<List<Cell>> {
        moveUpLeftTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle - 1) >= 0 }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle - 1]
            val currentCell = cellList[externalCycle][innerCycle]

            return@moveUpLeftTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    true
                }
                currentCell.value == shiftCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    false
                }
                else -> false
            }
        }

        return cellList
    }

    override fun right(cellList: List<List<Cell>>): List<List<Cell>> {
        moveDownRightTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle + 1]
            val currentCell = cellList[externalCycle][innerCycle]

            return@moveDownRightTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    true
                }
                currentCell.value == shiftCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    false
                }
                else -> false
            }
        }

        return cellList
    }

    override fun up(cellList: List<List<Cell>>): List<List<Cell>> {
        moveUpLeftTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle > 0) }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle - 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            return@moveUpLeftTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    true
                }
                shiftCell.value == currentCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    false
                }
                else -> false
            }
        }

        return cellList
    }

    override fun down(cellList: List<List<Cell>>): List<List<Cell>> {
        moveDownRightTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle + 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            return@moveDownRightTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    true
                }
                shiftCell.value == currentCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    false
                }
                else -> false
            }
        }

        return cellList
    }

    private fun moveDownRightTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                      whilePredicate: (innerCycle: Int) -> Boolean,
                                      blockWhile: (innerCycle: Int, externalCycle: Int) -> Boolean) {
        for (i in 0..(size - 1)) {
            for (j in (size - 2) downTo 0) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        if (blockWhile.invoke(temp, i)) temp++
                        else break
                    }
                }
            }
        }
    }

    private fun moveUpLeftTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                   whilePredicate: (innerCycle: Int) -> Boolean,
                                   blockWhile: (innerCycle: Int, externalCycle: Int) -> Boolean) {

        for (i in 0..(size - 1)) {
            for (j in 1..(size - 1)) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        if (blockWhile.invoke(temp, i)) temp--
                        else break
                    }
                }
            }
        }
    }

    private fun onSuccessMove(shiftCell: Cell, currentCell: Cell) {
        shiftCell.value *= 2
        currentCell.value = 0
    }

    private fun onEmptyMove(shiftCell: Cell, currentCell: Cell) {
        shiftCell.value = currentCell.value
        currentCell.value = 0
    }
}