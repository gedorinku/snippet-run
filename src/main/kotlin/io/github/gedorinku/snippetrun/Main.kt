package io.github.gedorinku.snippetrun

import io.github.gedorinku.snippetrun.runner.DockerImageBuilder
import io.github.gedorinku.snippetrun.slack.SlackBotService

/**
 * Created by gedorinku on 2017/12/03.
 */
fun main(args: Array<String>) {
    DockerImageBuilder().build()
    //SlackBotService().start()
}