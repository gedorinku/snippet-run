package io.github.gedorinku.snippetrun.slack

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Created by gedorinku on 2017/12/04.
 */
interface SlackApi {

    @GET("/{url}")
    fun fetchFile(@Header("Authorization") header: String, @Path("url", encoded = false) urlPrivate: String)
            : Call<ResponseBody>
}