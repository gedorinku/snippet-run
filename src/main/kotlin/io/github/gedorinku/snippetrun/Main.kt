package io.github.gedorinku.snippetrun

import io.github.gedorinku.snippetrun.runner.ExecuteCommand

/**
 * Created by gedorinku on 2017/12/03.
 */
fun main(args: Array<String>) {
    ExecuteCommand.COMMANDS.forEach { t, u ->
        println("$t -> $u")
    }
}