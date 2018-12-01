package presentation

import data.LiveData
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.browser.window


/**
 * Helper for async debounde.
 */
class Debounce() {
    private var isActive: Boolean = false

    fun execute(block: () -> Unit, timeout: Int) {
        if (!isActive) {
            isActive = true

            val onComplete = {
                block()
                isActive = false
            }
            window.setTimeout(onComplete, timeout)
        } else {
            console.log("break")
            return
        }
    }
}

/**
 * Helper for async throttle.
 */
class Throttle {
    private var timer: Int? = null

    fun execute(block: () -> Unit, timeout: Int) {
        timer?.let {
            window.clearTimeout(it)
            timer = null
        }

        if (timer == null) {
            val onComplete = {
                block()
                timer = null
            }

            timer = window.setTimeout(onComplete, timeout)
        }
    }
}

/**
 * Extension function for {@link LiveData<Int>} on increment value.
 * @param value increment value.
 */
fun LiveData<Int>.increment(value: Int) {
    setValue(getValue()?.run { this + value } ?: value)
}

/**
 * Extension function for clear canvas.
 */
fun CanvasRenderingContext2D.clear() {
    canvas.apply {
        clearRect(clientLeft.toDouble(),
                clientTop.toDouble(),
                width.toDouble(),
                height.toDouble())
    }
}