package domain

import data.*
import org.w3c.dom.CanvasRenderingContext2D

interface RenderServiceContract {

    interface RenderService<T : List<List<RenderServiceContract.DrawableElement>>> {
        fun startRender()
        fun stopRender()
        fun restartService()
        fun setRenderConfig(config: RenderServiceConfig)
        fun copyList(): T
        fun updateList(updatedList: T)
    }

    interface Transformer {
        enum class ActionMove {
            EMPTY_MOVE, SUCCESS_MOVE, FAILED_MOVE
        }

        fun left(cellList: List<List<Cell>>): List<List<Cell>>
        fun right(cellList: List<List<Cell>>): List<List<Cell>>
        fun up(cellList: List<List<Cell>>): List<List<Cell>>
        fun down(cellList: List<List<Cell>>): List<List<Cell>>
    }

    interface DrawableElement {
        val x: Double
        val y: Double
    }

    interface Drawer<T : DrawableElement> {
        fun drawElement(context: CanvasRenderingContext2D, element: T)
    }
}