package presentation

import data.LiveData
import kotlin.browser.window


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

fun LiveData<Int>.increment(value: Int) {
    setValue(getValue()?.run { this + value } ?: value)
}