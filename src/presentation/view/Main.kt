package presentation.view

import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import presentation.viewModel.MainViewModel
import presentation.MainContract
import kotlin.browser.document
import presentation.MainContract.Action

private lateinit var context: CanvasRenderingContext2D
private lateinit var viewModel: MainContract.ViewModel

fun main(args: Array<String>) {
    val canvas = document.getElementById("canvas")!! as HTMLCanvasElement
    context = canvas.getContext("2d")!! as CanvasRenderingContext2D

    val sizeInput = document.getElementById("size")!! as HTMLInputElement
    val changeSize = document.getElementById("change_size")!! as HTMLInputElement
    val scoreLabel = document.getElementById("score")!!
    val backArrow = document.getElementById("back_arrow")!! as HTMLImageElement

    val size = 4
    val cellWidth = canvas.width / size - 6
    val cellBorder = 6

    viewModel = MainViewModel(size, cellWidth, cellWidth, cellBorder, context)

    changeSize.onclick = {
        val value = try {
            sizeInput.value.toInt()
        } catch (e: Exception) {
            0
        }

        if (value >= 2 && value <= 20) {
            val newCellWidth = canvas.width / value - 6
            viewModel.onResize(value, newCellWidth, newCellWidth, cellBorder, context)
        }
    }

    backArrow.onclick = {
        viewModel.undo()
    }

    viewModel.scoreObservable.observe {
        scoreLabel.innerHTML = "Score : $it"
    }

    viewModel.loseObservable.observe {
        if (it) canvas.style.opacity = "0.5"
        else canvas.style.opacity = "1.0"
    }

    document.onkeydown = {
        if (it is KeyboardEvent) {
            when {
                it.keyCode == 38 || it.keyCode == 87 -> viewModel.actionObservable.setValue(Action.UP)
                it.keyCode == 39 || it.keyCode == 68 -> viewModel.actionObservable.setValue(Action.RIGHT)
                it.keyCode == 40 || it.keyCode == 83 -> viewModel.actionObservable.setValue(Action.DOWN)
                it.keyCode == 37 || it.keyCode == 65 -> viewModel.actionObservable.setValue(Action.LEFT)
            }
        }
    }
}

