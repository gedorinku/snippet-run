package io.github.gedorinku.snippetrun.runner

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

/**
 * Created by gedorinku on 2017/12/03.
 */
object Runner {

    private val workspaceDir = "/tmp/snippet-run"
    private const val timeoutSeconds = 3L
    private val queue = PublishSubject.create<Snippet>().apply {
        subscribeOn(Schedulers.io()).subscribe(Runner::run)
    }

    fun enqueue(executeCommand: ExecuteCommand,
                sourceCode: String,
                onComplete: (ProcessOutput) -> Unit) {
        queue.onNext(Snippet(executeCommand, sourceCode, onComplete))
    }

    private fun run(snippet: Snippet) {
        val (executeCommand, sourceCode) = snippet
        System.setProperty("jdk.lang.Process.allowAmbiguousCommands", "true")
        createWorkspaceDirectory()

        val containerId = createContainer(executeCommand.command)
        println("ContainerId: $containerId")

        copySourceCodeToContainer(executeCommand, sourceCode, containerId)

        val result = try {
            ProcessBuilder("docker start -i $containerId")
                    .executeByShell()
                    .waitOutputSync(timeout = timeoutSeconds + 1L)
        } finally {
            removeContainer(containerId)
        }

        snippet.onComplete(result)
    }

    private fun createWorkspaceDirectory() {
        ProcessBuilder("mkdir $workspaceDir")
                .executeByShell()
                .waitFor()
    }

    private fun createContainer(command: String): String {
        val dockerCommand =
                "docker create -i --net none --cpuset-cpus 0 --memory 256m --memory-swap 512m " +
                        "--pids-limit 10 --ulimit fsize=1000000 -w /tmp/workspace snippet-run-image " +
                        "timeout $timeoutSeconds su container -s /bin/sh -c '$command'"
        println(dockerCommand)

        //return container id
        return ProcessBuilder(dockerCommand)
                .executeByShell()
                .waitOutputSync()
                .output
                .substring(0, 12)
    }

    private fun copySourceCodeToContainer(executeCommand: ExecuteCommand, sourceCode: String, containerId: String) {
        val tempSourceFilePath = "$workspaceDir/${executeCommand.fileName}"

        File(tempSourceFilePath)
                .writeText(sourceCode)

        ProcessBuilder("docker cp $tempSourceFilePath $containerId:/tmp/workspace")
                .executeByShell()
                .waitFor()
    }

    private fun removeContainer(containerId: String) {
        ProcessBuilder("docker rm -f $containerId")
                .executeByShell()
                .waitFor()
    }

    private data class Snippet(val executeCommand: ExecuteCommand,
                               val sourceCode: String,
                               val onComplete: (ProcessOutput) -> Unit)
}