package domain

import data.Cell
import data.LiveData
import domain.RenderServiceContract.Transformer.ActionMove

class TransformerImpl(private val size: Int) : RenderServiceContract.Transformer {

    override val scoreChangedObservable = LiveData<Int>()

    override fun moveLeft(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveUpLeftTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle - 1) >= 0 }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle - 1]
            val currentCell = cellList[externalCycle][innerCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveUpLeftTemplate ActionMove.EMPTY_MOVE
            } else if (currentCell.value == shiftCell.value) {
                onSuccessMove(shiftCell, currentCell)
                return@moveUpLeftTemplate ActionMove.SUCCESS_MOVE
            } else {
                return@moveUpLeftTemplate ActionMove.FAILED_MOVE
            }
        }
    }

    override fun moveRight(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveDownRightTemplate({ i, j -> cellList[i][j] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[externalCycle][innerCycle + 1]
            val currentCell = cellList[externalCycle][innerCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveDownRightTemplate ActionMove.EMPTY_MOVE
            } else if (currentCell.value == shiftCell.value) {
                onSuccessMove(shiftCell, currentCell)
                return@moveDownRightTemplate ActionMove.SUCCESS_MOVE
            } else {
                return@moveDownRightTemplate ActionMove.FAILED_MOVE
            }
        }
    }

    override fun moveUp(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveUpLeftTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle > 0) }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle - 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]

            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveUpLeftTemplate ActionMove.EMPTY_MOVE
            } else if (shiftCell.value == currentCell.value) {
                onSuccessMove(shiftCell, currentCell)
                return@moveUpLeftTemplate ActionMove.SUCCESS_MOVE
            } else {
                return@moveUpLeftTemplate ActionMove.FAILED_MOVE
            }
        }
    }

    override fun moveDown(cellList: MutableList<MutableList<Cell>>): List<ActionMove> {
        return moveDownRightTemplate({ i, j -> cellList[j][i] },
                { innerCycle -> (innerCycle + 1) < size }) { innerCycle, externalCycle ->
            val shiftCell = cellList[innerCycle + 1][externalCycle]
            val currentCell = cellList[innerCycle][externalCycle]
            if (shiftCell.value == 0) {
                shiftCell.value = currentCell.value
                currentCell.value = 0
                return@moveDownRightTemplate ActionMove.EMPTY_MOVE
            } else if (shiftCell.value == currentCell.value) {
                onSuccessMove(shiftCell, currentCell)
                return@moveDownRightTemplate ActionMove.SUCCESS_MOVE
            } else {
                return@moveDownRightTemplate ActionMove.FAILED_MOVE
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
        scoreChangedObservable.setValue(shiftCell.value)
    }
}