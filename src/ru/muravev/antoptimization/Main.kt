package ru.muravev.antoptimization


fun main() {
    val optimizer = AntOptimizer(
        ants = 100,
        iter = 20,
        a = 1.5,
        b = 1.2,
        p = 0.6,
        q = 10.0
    )

    val path = optimizer.run()
    path.print()
}