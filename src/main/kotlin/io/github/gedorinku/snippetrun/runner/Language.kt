package io.github.gedorinku.snippetrun.runner

/**
 * Created by gedorinku on 2017/12/11.
 */
data class Language(val name: String = "",
                    val filename: String = "",
                    val executeCommand: String = "",
                    val versionCommand: String = "") {

    val filenameExtension: String by lazy {
        val index = filename.indexOf('.')
        if (index == -1 || index == filename.lastIndex) {
            ""
        } else {
            filename.substring(index + 1)
        }
    }
}