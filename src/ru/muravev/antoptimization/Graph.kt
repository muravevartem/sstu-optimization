package ru.muravev.antoptimization

import java.io.File

object Graph {
    fun read(): List<List<Double>> {
        return File("resources/graph.csv").readLines()
            .map { line ->
                line.split(";")
                    .map { it.asWeight() }
            }
    }

    private fun String.asWeight(): Double {
        val rawValue = this.toDouble()
        if (rawValue == 0.0) {
            return Double.POSITIVE_INFINITY
        }
        return rawValue
    }
}