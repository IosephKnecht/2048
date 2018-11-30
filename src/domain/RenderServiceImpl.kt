package domain

import data.Cell
import data.RenderServiceConfig
import presentation.clear
import kotlin.browser.window

class RenderServiceImpl(private var config: RenderServiceConfig,
                        private val drawer: RenderServiceContract.Drawer<Cell>) : RenderServiceContract.RenderService<List<List<Cell>>>{

    private var cellList = mutableListOf<MutableList<Cell>>()
    private var requestAnimationFrameValue: Int? = null

    override fun startRender() {
        reset()
        createCells()
        animate()
    }

    override fun stopRender() {
        window.cancelAnimationFrame(requestAnimationFrameValue!!)
    }

    override fun restartService() {
        requestAnimationFrameValue?.let { window.cancelAnimationFrame(it) }

        reset()

        animate()
    }

    override fun setRenderConfig(config: RenderServiceConfig) {
        this.config = config
        (drawer as RectDrawer).updateParams(config.cellWidth, config.cellHeight)
    }

    override fun updateList(updatedList: List<List<Cell>>) {
        this.cellList = updatedList.map { it.toMutableList() }.toMutableList()
    }

    private fun reset() {
        config.context.clear()
        cellList.clear()
    }

    private fun drawAllCells() {
        cellList.forEach {
            it.forEach { cell -> drawer.drawElement(config.context, cell) }
        }
    }

    private fun createCells() {
        for (i in 0..(config.size - 1)) {
            cellList.add(mutableListOf())
            for (j in 0..(config.size - 1)) {
                cellList[i].add(createEmptyCell(i, j))
            }
        }
    }

    override fun copyList(): List<List<Cell>> {
        return cellList.map { it.map { Cell(it.x, it.y, it.value) } }
    }

    private fun createEmptyCell(row: Int, coll: Int): Cell {
        val x = coll * config.cellWidth + config.cellBorder * (coll + 1)
        val y = row * config.cellHeight + config.cellBorder * (row + 1)
        return Cell(x, y, 0)
    }

    private fun animate() {
        requestAnimationFrameValue = window.requestAnimationFrame {
            animate()
        }
        config.context.clear()
        drawAllCells()
    }
}