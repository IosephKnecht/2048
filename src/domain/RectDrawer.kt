package domain

import data.Cell
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign

class RectDrawer(private var cellWidth: Double,
                 private var cellHeight: Double) : RenderServiceContract.Drawer<Cell> {

    fun updateParams(cellWidth: Double, cellHeight: Double) {
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
    }

    override fun drawElement(context: CanvasRenderingContext2D, element: Cell) {
        context.beginPath()

        context.rect(element.x,
                element.y,
                cellWidth,
                cellHeight)

        when (element.value) {
            0 -> context.fillStyle = "#F3F35F"
            2 -> context.fillStyle = "#488281"
            4 -> context.fillStyle = "#C46B3E"
            8 -> context.fillStyle = "#FF4949"
            16 -> context.fillStyle = "#9966CC"
            32 -> context.fillStyle = "#5FACF3"
            64 -> context.fillStyle = "#8DB600"
            128 -> context.fillStyle = "#7BA05B"
            256 -> context.fillStyle = "#00D3B5"
            512 -> context.fillStyle = "#7FFFD4"
            1024 -> context.fillStyle = "#4B5320"
            2048 -> context.fillStyle = "#3B444B"
            4096 -> context.fillStyle = "#0000FF"
            else -> context.fillStyle = "#007FFF"
        }

        context.fill()

        if (element.value != 0) {
            val fontSize = cellWidth / 2f
            context.font = "${fontSize}px Arial"
            context.fillStyle = "white"
            context.textAlign = CanvasTextAlign.CENTER
            context.fillText(element.value.toString(), (element.x + cellWidth / 2), (element.y + cellWidth / 2))
        }
    }
}