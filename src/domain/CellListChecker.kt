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
                if (list[i][j].value == list[i][j + 1].value) return true
            }
        }

        return false
    }

    fun checkRow(list: List<List<Cell>>): Boolean {
        val size = list.size

        for (j in 0..(size - 1)) {
            for (i in 0..(size - 2)) {
                if (list[i][j].value == list[i + 1][j].value) return true
            }
        }

        return false
    }

}