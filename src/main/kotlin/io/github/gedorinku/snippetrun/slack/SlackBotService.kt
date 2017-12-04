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
import java.util.*

/**
 * Created by gedorinku on 2017/12/04.
 */
class SlackBotService {

    lateinit var slackClient: SlackApiClient

    fun start() {
        val apiToken = loadApiToken()
        val session = SlackSessionFactory.createWebSocketSlackSession(apiToken)
        session.connect()
        slackClient = SlackApiClient(apiToken)
        session.addMessagePostedListener { event, session ->
            if (event.messageSubType != SlackMessagePosted.MessageSubType.FILE_SHARE ||
                    !event.slackFile.mimetype.startsWith("text/")) {
                return@addMessagePostedListener
            }

            tryRunSnippet(session, event.channel, event.slackFile)
        }
    }

    private fun tryRunSnippet(session: SlackSession, channel: SlackChannel, slackFile: SlackFile) = async {
        val extension = getFileNameExtension(slackFile.name)
        val executeCommand = ExecuteCommand.COMMANDS[extension] ?: return@async
        val sourceCode = slackClient.fetchTextFile(slackFile.urlPrivate)

        val result = try {
            Runner.run(executeCommand, sourceCode)
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
        session.sendMessage(channel, "error output:\n${result.errorOutput}")
        session.sendMessage(channel, "output:\n${result.output}")
    }

    private fun loadApiToken(): String =
            Properties().run {
                load(File("secrets.properties").inputStream())
                getProperty("slackApiToken", "")
            }

    private fun getFileNameExtension(name: String): String {
        val index = name.lastIndexOf('.')
        return if (index == -1) {
            ""
        } else {
            name.substring(index + 1, name.length)
        }
    }
}