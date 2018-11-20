package domain

interface Transformer {
    enum class ActionMove {
        EMPTY_MOVE, SUCCESS_MOVE, FAILED_MOVE
    }

    fun moveLeft()
    fun moveRight()
    fun moveUp()
    fun moveDown()
}