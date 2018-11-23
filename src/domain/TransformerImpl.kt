package domain

import data.Cell
import data.LiveData
import domain.RenderServiceContract.Transformer.ActionMove

class TransformerImpl(private val size: Int) : RenderServiceContract.Transformer {

    override fun left(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveUpLeftTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle - 1) >= 0 }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle - 1]
            val currentCell = cellList[externalCycle][innerCycle]

            return@moveUpLeftTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    ActionMove.EMPTY_MOVE
                }
                currentCell.value == shiftCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    ActionMove.SUCCESS_MOVE
                }
                else -> ActionMove.FAILED_MOVE
            }
        }
    }

    override fun right(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveDownRightTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle + 1]
            val currentCell = cellList[externalCycle][innerCycle]

            return@moveDownRightTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    ActionMove.EMPTY_MOVE
                }
                currentCell.value == shiftCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    ActionMove.SUCCESS_MOVE
                }
                else -> ActionMove.FAILED_MOVE
            }
        }
    }

    override fun up(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveUpLeftTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle > 0) }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle - 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            return@moveUpLeftTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    ActionMove.EMPTY_MOVE
                }
                shiftCell.value == currentCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    ActionMove.SUCCESS_MOVE
                }
                else -> ActionMove.FAILED_MOVE
            }
        }
    }

    override fun down(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveDownRightTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle + 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            return@moveDownRightTemplate when {
                shiftCell.value == 0 -> {
                    onEmptyMove(shiftCell, currentCell)
                    ActionMove.EMPTY_MOVE
                }
                shiftCell.value == currentCell.value -> {
                    onSuccessMove(shiftCell, currentCell)
                    ActionMove.SUCCESS_MOVE
                }
                else -> ActionMove.FAILED_MOVE
            }
        }
    }

    private fun moveDownRightTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                      whilePredicate: (innerCycle: Int) -> Boolean,
                                      blockWhile: (innerCycle: Int, externalCycle: Int) -> ActionMove): List<ActionMove> {
        val actionMoveList = mutableListOf<ActionMove>()

        for (i in 0..(size - 1)) {
            for (j in (size - 2) downTo 0) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        val actionMove = blockWhile.invoke(temp, i)
                        actionMoveList.add(actionMove)

                        if (actionMove == ActionMove.EMPTY_MOVE) temp++
                        else break
                    }
                }
            }
        }

        return actionMoveList
    }

    private fun moveUpLeftTemplate(startWhilePredicate: (i: Int, j: Int) -> Cell,
                                   whilePredicate: (innerCycle: Int) -> Boolean,
                                   blockWhile: (innerCycle: Int, externalCycle: Int) -> ActionMove): List<ActionMove> {
        val actionMoveList = mutableListOf<ActionMove>()

        for (i in 0..(size - 1)) {
            for (j in 1..(size - 1)) {
                var temp = j
                if (startWhilePredicate.invoke(i, j).value != 0) {
                    while (whilePredicate.invoke(temp)) {
                        val actionMove = blockWhile.invoke(temp, i)
                        actionMoveList.add(actionMove)
                        if (actionMove == ActionMove.EMPTY_MOVE) temp--
                        else break
                    }
                }
            }
        }

        return actionMoveList
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