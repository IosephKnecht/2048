package domain

import data.Cell

object CellListChecker {

    fun isEmpty(list: List<List<Cell>>): Boolean {
        list.forEach {
            it.forEach {
                if (it.value == 0) return true
            }
        }
        return false
    }

    fun checkColl(list: List<List<Cell>>): Boolean {
        val size = list.size

        for (i in 0..(size - 1)) {
            for (j in 0..(size - 2)) {
                val currentCell = list[i][j]
                val nextCell = list[i][j + 1]

                if (currentCell.value == 0 ||
                        nextCell.value == 0 ||
                        currentCell.value == nextCell.value) return true
            }
        }

        return false
    }

    fun checkRow(list: List<List<Cell>>): Boolean {
        val size = list.size

        for (j in 0..(size - 1)) {
            for (i in 0..(size - 2)) {
                val currentCell = list[i][j]
                val nexCell = list[i + 1][j]

                if (currentCell.value == 0 ||
                        nexCell.value == 0 ||
                        currentCell.value == nexCell.value) return true
            }
        }

        return false
    }

}