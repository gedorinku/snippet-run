package io.github.gedorinku.snippetrun.slack

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackFile
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory
import io.github.gedorinku.snippetrun.runner.ExecuteCommand
import io.github.gedorinku.snippetrun.runner.Runner
import kotlinx.coroutines.experimental.async
import java.io.File
import java.net.URL
import java.util.*

/**
 * Created by gedorinku on 2017/12/04.
 */
class SlackBotService {

    fun start() {
        val session = SlackSessionFactory.createWebSocketSlackSession(loadApiToken())
        session.connect()
        session.addMessagePostedListener { event, session ->
            if (event.messageSubType != SlackMessagePosted.MessageSubType.FILE_SHARE ||
                    !event.slackFile.mimetype.startsWith("text/")) {
                return@addMessagePostedListener
            }

            tryRunSnippet(session, event.channel, event.slackFile)
        }
    }

    private fun tryRunSnippet(session: SlackSession, channel: SlackChannel, slackFile: SlackFile) = async {
        val extention = getFileNameExtention(slackFile.name)
        val executeCommand = ExecuteCommand.COMMANDS[extention] ?: return@async
        val sourceCode = URL(slackFile.permalinkPublic).openStream().bufferedReader().use {
            it.readText()
        }

        val result = try {
            Runner.run(executeCommand, sourceCode)
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
        session.sendMessage(channel, result.output)
    }

    private fun loadApiToken(): String =
            Properties().run {
                load(File("secrets.properties").inputStream())
                getProperty("slackApiToken", "")
            }

    private fun getFileNameExtention(name: String): String {
        val index = name.lastIndexOf('.')
        return if (index == -1) {
            ""
        } else {
            name.substring(index + 1, name.length)
        }
    }
}