package data

import org.w3c.dom.CanvasRenderingContext2D

data class RenderServiceConfig(val size: Int,
                               val cellWidth: Double,
                               val cellHeight: Double,
                               val cellBorder: Double,
                               val context: CanvasRenderingContext2D)