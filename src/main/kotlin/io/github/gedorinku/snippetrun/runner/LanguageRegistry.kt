package io.github.gedorinku.snippetrun.runner

import com.google.gson.Gson
import java.io.File

/**
 * Created by gedorinku on 2017/12/11.
 */
object LanguageRegistry {

    val languages: List<Language> = loadLanguages()

    fun findLanguage(filenameExtension: String): Language? =
            languages.find { it.filenameExtension == filenameExtension }

    private fun loadLanguages(): List<Language> =
            File("languages.json")
                    .inputStream()
                    .reader()
                    .use {
                        Gson().fromJson(it, Array<Language>::class.java)
                    }
                    .toList()
}