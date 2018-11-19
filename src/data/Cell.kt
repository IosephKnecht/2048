package data

data class Cell(val x: Int,
                val y: Int,
                var value: Int = 0) {

    override fun equals(other: Any?): Boolean {
        val obj = other is Cell
        if (!obj) return false

        val origin = other as Cell

        return x == origin.x &&
                y == origin.y &&
                value == origin.value
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + value
        return result
    }
}