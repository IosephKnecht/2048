package presentation.view

import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import presentation.Debounce
import presentation.MainContract.Action
import presentation.Throttle
import presentation.viewModel.MainViewModel
import kotlin.browser.document
import kotlin.browser.window

private lateinit var canvas: HTMLCanvasElement
private lateinit var loseHolder: HTMLDivElement
private lateinit var winHolder: HTMLDivElement

fun main(args: Array<String>) {
    canvas = document.getElementById("canvas")!! as HTMLCanvasElement
    val context = canvas.getContext("2d")!! as CanvasRenderingContext2D

    val sizeInput = document.getElementById("size")!! as HTMLInputElement
    val changeSize = document.getElementById("change_size")!! as HTMLInputElement
    val scoreLabel = document.getElementById("score")!!
    val backArrow = document.getElementById("back_arrow")!! as HTMLImageElement
    val closeButton = document.getElementById("close_button")!! as HTMLImageElement
    val reloadButton = document.getElementById("restart_button")!! as HTMLImageElement

    loseHolder = document.getElementById("lose_holder") as HTMLDivElement
    winHolder = document.getElementById("win_holder") as HTMLDivElement

    val size = 4
    val cellBorder = 5.0
    val cellWidth = canvas.width / size - (cellBorder + 1)

    val viewModel = MainViewModel(size, cellWidth, cellWidth, cellBorder, context)

    changeSize.onclick = {
        val value = try {
            sizeInput.value.toInt()
        } catch (e: Exception) {
            0
        }

        if (value >= 2 && value <= 20) {
            val newCellWidth = canvas.width / value - (cellBorder + 1)
            viewModel.onResize(value, newCellWidth, newCellWidth, cellBorder, context)
        }
    }

    winHolder.onclick = {
        viewModel.onWinHolderClick()
    }

    backArrow.onclick = { viewModel.undo() }

    reloadButton.onclick = {
        viewModel.reload()
    }

    closeButton.onclick = {
        window.close()
    }

    viewModel.scoreObservable.observe {
        scoreLabel.innerHTML = "Score : $it"
    }

    viewModel.loseObservable.observe {
        onLoseChangeState(it)
    }

    viewModel.winObservable.observe {
        onWinChangeState(it)
    }

    val keyDownEvent: (KeyboardEvent) -> Unit = {
        when {
            it.keyCode == 38 || it.keyCode == 87 -> viewModel.actionObservable.setValue(Action.UP)
            it.keyCode == 39 || it.keyCode == 68 -> viewModel.actionObservable.setValue(Action.RIGHT)
            it.keyCode == 40 || it.keyCode == 83 -> viewModel.actionObservable.setValue(Action.DOWN)
            it.keyCode == 37 || it.keyCode == 65 -> viewModel.actionObservable.setValue(Action.LEFT)
        }
    }

    val keyBoardThrottle = Throttle()

    document.onkeydown = {
        if (it is KeyboardEvent) {
            keyBoardThrottle.execute({ keyDownEvent.invoke(it) }, 100)
        }
    }
}

private fun onLoseChangeState(isLose: Boolean) {
    if (isLose) {
        loseHolder.style.opacity = "0.85"
        loseHolder.style.visibility = "visible"
    } else {
        loseHolder.style.visibility = "hidden"
    }
}

private fun onWinChangeState(isWin: Boolean) {
    if (isWin) {
        winHolder.style.opacity = "0.85"
        winHolder.style.visibility = "visible"
    } else {
        winHolder.style.visibility = "hidden"
    }
}

