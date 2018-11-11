package data

import org.w3c.dom.CanvasRenderingContext2D

data class RendrerServiceConfig(val size: Int,
                                val cellWidth: Int,
                                val cellHeight: Int,
                                val cellBorder: Int,
                                val context: CanvasRenderingContext2D)