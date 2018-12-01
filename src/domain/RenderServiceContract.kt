package domain

import data.*
import org.w3c.dom.CanvasRenderingContext2D

interface RenderServiceContract {

    interface RenderService<T : List<List<RenderServiceContract.DrawableElement>>> {
        /**
         * Method for start service.
         */
        fun startRender()

        /**
         * Method for stop service.
         */
        fun stopRender()

        /**
         * Method for set new config.
         * @param config new config
         */
        fun setRenderConfig(config: RenderServiceConfig)

        /**
         * Additional method, for shallow copy list.
         * @return shallow copy list.
         */
        fun copyList(): T

        /**
         * Method for update list for render.
         * @param updatedList new frame.
         */
        fun updateList(updatedList: T)
    }

    interface Transformer<E : DrawableElement, T : List<List<E>>> {
        /**
         * Bad solve. Observable for changeState inner list.
         */
        val transformChangeObservable: ImmutableLiveData<Any>

        /**
         * Method for transform block to left.
         * @param cellList transformable list.
         * @return transformed list.
         */
        fun left(cellList: T): T

        /**
         * Method for transform block to right.
         * @param cellList transformable list.
         * @return transformed list.
         */
        fun right(cellList: T): T

        /**
         * Method for transform block to up.
         * @param cellList transformable list.
         * @return transformed list.
         */
        fun up(cellList: T): T

        /**
         * Method for transform block to down.
         * @param cellList transformable list.
         * @return transformed list.
         */
        fun down(cellList: T): T
    }

    /**
     * Interface for abstract drawable element.
     */
    interface DrawableElement {
        val x: Double
        val y: Double
    }

    /**
     * Interface for abstract drawer object.
     */
    interface Drawer<T : DrawableElement> {
        /**
         * Method for draw abstract {@link DrawerElement}
         */
        fun drawElement(context: CanvasRenderingContext2D, element: T)
    }
}