package io.github.gedorinku.snippetrun.runner

import java.io.File

/**
 * Created by gedorinku on 2017/12/03.
 */
object Runner {

    private val workspaceDir = "/tmp/snippet-run"

    fun run(executeCommand: ExecuteCommand, sourceCode: String): ProcessOutput {
        System.setProperty("jdk.lang.Process.allowAmbiguousCommands", "true")
        val containerId = createContainer(executeCommand.command)
        println("ContainerId: $containerId")

        copySourceCodeToContainer(executeCommand, sourceCode, containerId)

        val output = ProcessBuilder("docker start -i $containerId")
                .executeByShell()
                .waitOutputSync(timeout = 5)

        ProcessBuilder("docker rm -f $containerId")
                .executeByShell()
                .waitFor()

        return output
    }

    private fun createContainer(command: String): String {
        val dockerCommand =
                "docker create -i --net none --cpuset-cpus 0 --memory 256m --memory-swap 512m " +
                        "--ulimit nproc=75 --ulimit fsize=1000000 -w /tmp/workspace snippet-run-image " +
                        "timeout 3 su container -s /bin/sh -c '$command'"
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
}