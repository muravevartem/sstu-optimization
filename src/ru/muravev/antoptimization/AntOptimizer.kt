package ru.muravev.antoptimization

import java.lang.Math.random
import kotlin.math.pow

/**
 * ants - кол-во муравьев
 * iter - максимаьлное кол-во итераций алгоритма
 * a - влияние "феромона" на ребре
 * b - влияние привлекательности маршрута
 * p - испарение феромона
 * q - интенсивность "феромона"
 */
class AntOptimizer(
    val ants: Int, val iter: Int,
    val a: Double, val b: Double,
    val p: Double, val q: Double
) {
    /**
     * Выбор пути
     */
    private fun selectIndex(selection: List<Int>): Int {
        val sumNum = selection.sum()
        if (sumNum == 0) {
            return selection.size - 1
        }

        val tmpNum = random()
        var prob = 0
        for (i in selection.indices) {
            prob += selection[i] / sumNum
            if (prob >= tmpNum) {
                return i
            }
        }
        return 0
    }

    /**
     * Составление маршрута (индексов вершин)
     */
    private fun createIndex(dm: List<List<Double>>, pm: List<List<Double>>): List<Int> {
        val l = dm.size
        val unvisitedIndex = (0..<l).toMutableList()
        unvisitedIndex.shuffle()
        val visitedIndex = mutableListOf(unvisitedIndex.removeLast())
        for (anonymous in 0..<l - 1) {
            val i = visitedIndex.last()
            val selection = mutableListOf<Int>()
            for (j in unvisitedIndex) {
                selection.add(
                    ((pm[i][j].pow(this.a)) * ((1 / dm[i][j].coerceAtLeast(10.0.pow(-5.0)).pow(this.b)))).toInt()
                )
            }
            val selectedIndex = selectIndex(selection)
            visitedIndex.add(unvisitedIndex.removeAt(selectedIndex))
        }
        visitedIndex.add(visitedIndex.first())
        return visitedIndex
    }

    /**
     * Обновление матрицы "феромонов"
     * Кол-во "феромона" на i -> j = (1 - скорость испарения) * Кол-во "феромана" на (i, j) + интенсивность / длина маршрута (i -> j)
     */
    private fun updatePm(pm: MutableList<MutableList<Double>>, tmpIndex: List<List<Int>>, tmpLength: List<Double>) {
        val l = pm.size
        for (i in 0..<l) {
            for (j in i..<l) {
                pm[i][j] *= 1 - this.p
                pm[j][i] *= 1 - this.p
            }
        }

        for (i in 0..<this.ants) {
            val delta = this.q / tmpLength[i]
            val index = tmpIndex[i]
            for (j in 0..<l) {
                pm[index[j]][index[j + 1]] += delta
                pm[index[j + 1]][index[j]] += delta
            }
        }
    }

    /**
     * Считает расстояние пройденное по маршруту
     */
    private fun calculateDistance(dm: List<List<Double>>, index: List<Int>): Double {
        var distance = 0.0
        for (i in 0..<index.size - 1) {
            distance += dm[index[i]][index[i + 1]]
        }
        return distance
    }

    fun run(): Path {
        val dm = Graph.read()
        val l = dm.size
        val pm = MutableList(l) {
            MutableList(l) {
                1.0
            }
        }
        var resIndex = listOf<Int>()
        var resLength = Double.POSITIVE_INFINITY
        for (x in 0..<this.iter) {
            val tmpIndex = mutableListOf<List<Int>>()
            val tmpLength = mutableListOf<Double>()
            for (y in 0..<this.ants) {
                val index = this.createIndex(dm, pm)
                tmpIndex.add(index)
                tmpLength.add(calculateDistance(dm, index))
            }
            updatePm(pm, tmpIndex, tmpLength)
            val bestLength = tmpLength.min()
            if (bestLength < resLength) {
                resLength = bestLength
                resIndex = tmpIndex[tmpLength.indexOf(bestLength)]
            }
        }
        return Path(resIndex, resLength)
    }


}