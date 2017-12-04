package io.github.gedorinku.snippetrun.slack

import retrofit2.Retrofit

/**
 * Created by gedorinku on 2017/12/04.
 */
class SlackApiClient(private val apiToken: String) {

    private val api: SlackApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://files.slack.com")
                .build()
        api = retrofit.create(SlackApi::class.java)
    }

    fun fetchTextFile(urlPrivate: String): String {
        val header = "Bearer $apiToken"
        val startIndex = "https://files.slack.com".length
        val body = api.fetchFile(header, urlPrivate.substring(startIndex)).execute().body() ?: return ""
        return body.string()
    }
}