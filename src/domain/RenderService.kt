package domain

import data.CacheModel

interface RenderService {
    fun startRender()
    fun stopRender()
    fun restartService()
    fun restoreState(cacheModel: CacheModel)
}