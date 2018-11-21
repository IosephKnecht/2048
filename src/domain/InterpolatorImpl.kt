package domain

import data.Cell
import kotlin.browser.window

class InterpolatorImpl : Interpolator {
    private val offset = 10.0

    fun onAction(action: Interpolator.Action,
                 currentCell: Cell,
                 shiftCell: Cell) {
        window.setInterval({
            when (action) {
                Interpolator.Action.LEFT -> left(currentCell, shiftCell)
                Interpolator.Action.RIGHT -> right(currentCell, shiftCell)
                Interpolator.Action.UP -> up(currentCell, shiftCell)
                Interpolator.Action.DOWN -> down(currentCell, shiftCell)
            }
        }, 20)
    }

    private fun left(currentCell: Cell, shiftCell: Cell) {
        if (currentCell.x > shiftCell.x) {
            currentCell.x -= offset
        } else {
            window.clearInterval(20)
        }
    }

    private fun right(currentCell: Cell, shiftCell: Cell) {
        if (currentCell.x < shiftCell.x) {
            currentCell.x += offset
        } else {
            window.clearInterval(20)
        }
    }

    private fun down(currentCell: Cell, shiftCell: Cell) {
        if (currentCell.y < shiftCell.y) {
            currentCell.y += offset
        } else {
            window.clearInterval(20)
        }
    }

    private fun up(currentCell: Cell, shiftCell: Cell) {
        if (currentCell.y > shiftCell.y) {
            currentCell.y -= offset
        } else {
            window.clearInterval(20)
        }
    }
}