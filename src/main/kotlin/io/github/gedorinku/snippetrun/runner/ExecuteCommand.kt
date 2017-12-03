package io.github.gedorinku.snippetrun.runner

import java.io.File
import java.util.*

/**
 * Created by gedorinku on 2017/12/03.
 */
data class ExecuteCommand(val languageTag: String, val fileName: String, val command: String) {

    companion object {

        val COMMANDS: Map<String, ExecuteCommand> by lazy { loadCommands() }

        private fun loadCommands(): Map<String, ExecuteCommand> {
            val commands = mutableMapOf<String, ExecuteCommand>()

            File("execute_commands.txt").bufferedReader().use {
                var lineNumber = 0
                while (true) {
                    lineNumber++
                    val line = it.readLine()?.trim() ?: return@use

                    if (line.isEmpty() || line.startsWith('#')) {
                        continue
                    }

                    val parameters = line.split(' ')
                    if (parameters.size < 3) {
                        throw InvalidPropertiesFormatException("excute_commands.txt: line $lineNumber")
                    }

                    val languageTag = parameters[0]
                    val fileName = parameters[1]
                    val command = parameters.subList(2, parameters.size).joinToString(" ", "")
                    commands.put(languageTag, ExecuteCommand(languageTag, fileName, command))
                }
            }

            return commands
        }
    }
}