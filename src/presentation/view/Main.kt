package presentation.view

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import presentation.viewModel.MainViewModel
import presentation.MainContract
import kotlin.browser.document
import presentation.MainContract.Action

private var width = 0
private var size = 0
private lateinit var context: CanvasRenderingContext2D
private lateinit var viewModel: MainContract.ViewModel

fun main(args: Array<String>) {
    val canvas = document.getElementById("canvas")!! as HTMLCanvasElement
    context = canvas.getContext("2d")!! as CanvasRenderingContext2D

    val scoreLabel = document.getElementById("score")!!

    val size = 4
    val cellWidth = canvas.width / size - 6
    val cellBorder = 6

    viewModel = MainViewModel(size, cellWidth, cellWidth, cellBorder, context)

    viewModel.scoreObservable.observe {
        scoreLabel.innerHTML = "Score : $it"
    }

    viewModel.loseObservable.observe {
        canvas.style.opacity = "0.5"
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

//    val sizeInput = document.getElementById("presentation.view.size")!!
//    val changeSize = document.getElementById("change_size")!!
//    val scoreLabel = document.getElementById("score")!!
//
//    var score = 0
//    size = 4
//    width = canvas.width / size - 6
//
//    var lose = false
//
//    startGame()
//
//    document.onkeydown = {
//        if (it is KeyboardEvent && !lose) {
//            when {
//                it.keyCode == 38 || it.keyCode == 87 -> moveUp()
//                it.keyCode == 39 || it.keyCode == 68 -> moveRight()
//                it.keyCode == 40 || it.keyCode == 83 -> moveDown()
//                it.keyCode == 37 || it.keyCode == 65 -> moveLeft()
//            }
//            //FIXME: update score
//        }
//    }
}

//fun startGame() {
//    createCells()
//    drawAllCells()
//    pasteNewCell()
//}

