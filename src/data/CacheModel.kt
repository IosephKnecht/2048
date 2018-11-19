package data

data class CacheModel(val cellList: MutableList<MutableList<Cell>> = mutableListOf(),
                      val score: Int = 0)