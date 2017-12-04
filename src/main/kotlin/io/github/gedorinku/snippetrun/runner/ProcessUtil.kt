package io.github.gedorinku.snippetrun.runner

import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by gedorinku on 2017/12/03.
 */

data class ProcessOutput(val output: String, val errorOutput: String)

/**
 * プロセスが終了するまで待って標準出力を返します。
 * @param outputLimit 標準出力をoutputLimit bytesで打ち切ります。(デフォルトは256)
 */
fun Process.waitOutputSync(outputLimit: Int = 256): ProcessOutput {
    return try {
        waitFor(10, TimeUnit.SECONDS)

        val output = readString(inputStream, outputLimit)
        val errorOutput = readString(errorStream, outputLimit)
        ProcessOutput(output, errorOutput)
    } finally {
        destroy()
    }
}

private fun readString(inputStream: InputStream, limit: Int): String = inputStream.use {
    val bytes = mutableListOf<Byte>()
    var next = it.read()
    while (bytes.size < limit && next != -1) {
        bytes.add(next.toByte())
        next = it.read()
    }

    String(bytes.toByteArray())
}

/**
 * Runtime.executeを使うとなぜかうまくいかないので、実行したいコマンドを/bin/shの標準入力に流す。
 */
fun ProcessBuilder.executeByShell(): Process {
    val process = Runtime.getRuntime().exec("/bin/sh")
    try {
        process.outputStream.bufferedWriter().use {
            it.write(this.command().joinToString(" "))
            it.newLine()
            it.write("exit")
            it.newLine()
        }
    } catch (t: Throwable) {
        process.destroy()
        throw t
    }

    return process
}