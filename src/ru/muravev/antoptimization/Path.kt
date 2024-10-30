package ru.muravev.antoptimization

data class Path(
    val index: List<Int>,
    val length: Double
) {
    fun print() {
        print("Оптимальный путь $length [ ${index.joinToString(separator = " -> ") { (it + 1).toString() }} ]")
    }
}
