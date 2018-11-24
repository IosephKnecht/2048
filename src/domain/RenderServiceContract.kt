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

    interface Transformer<E : DrawableElement, T : List<List<E>>> {
        val transformChangeObservable: ImmutableLiveData<Any>

        fun left(cellList: T): T
        fun right(cellList: T): T
        fun up(cellList: T): T
        fun down(cellList: T): T
    }

    interface DrawableElement {
        val x: Double
        val y: Double
    }

    interface Drawer<T : DrawableElement> {
        fun drawElement(context: CanvasRenderingContext2D, element: T)
    }
}