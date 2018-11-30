package data

import domain.RenderServiceContract

data class Cell(override val x: Double,
                override val y: Double,
                var value: Int = 0) : RenderServiceContract.DrawableElement {

    override fun equals(other: Any?): Boolean {
        val obj = other is Cell
        if (!obj) return false

        val origin = other as Cell

        return x == origin.x &&
                y == origin.y &&
                value == origin.value
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

}