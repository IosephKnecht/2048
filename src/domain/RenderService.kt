package domain

import data.CacheModel

interface RenderService {
    fun drawAllCells()
    fun createCells()
    fun pasteNewCell()
    fun restoreState(cacheModel: CacheModel)
    fun reset()
}