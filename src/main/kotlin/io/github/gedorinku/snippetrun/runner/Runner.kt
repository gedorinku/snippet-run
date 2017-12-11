package io.github.gedorinku.snippetrun.runner

import java.io.File

/**
 * Created by gedorinku on 2017/12/03.
 */
object Runner {

    private val workspaceDir = "/tmp/snippet-run"
    private const val timeoutSeconds = 3L

    fun run(executeCommand: ExecuteCommand, sourceCode: String): ProcessOutput {
        System.setProperty("jdk.lang.Process.allowAmbiguousCommands", "true")
        createWorkspaceDirectory()

        val containerId = createContainer(executeCommand.command)
        println("ContainerId: $containerId")

        copySourceCodeToContainer(executeCommand, sourceCode, containerId)

        return try {
            ProcessBuilder("docker start -i $containerId")
                    .executeByShell()
                    .waitOutputSync(timeout = timeoutSeconds + 1L)
        } finally {
            removeContainer(containerId)
        }
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
}