package io.github.gedorinku.snippetrun

import io.github.gedorinku.snippetrun.runner.ExecuteCommand
import io.github.gedorinku.snippetrun.runner.Runner

/**
 * Created by gedorinku on 2017/12/03.
 */
fun main(args: Array<String>) {
    val sourceCode = """
        #include <unistd.h>
        #include <stdio.h>
        int main() {while(1) {fork();}}
    """.trimIndent()
    val result = Runner.run(ExecuteCommand.COMMANDS["c"]!!, sourceCode)
    println(result)
}