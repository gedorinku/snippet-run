package io.github.gedorinku.snippetrun.runner

/**
 * Created by gedorinku on 2017/12/13.
 */
class DockerImageBuilder {

    companion object {

        const val dockerImageNameHeader = "snippet-run-image-"
    }

    private val containersDirectory = "./containers"

    fun build() {
        val sorted = TopologicalSorting(LanguageRegistry.languages).sort()

        val imageCount = sorted.size + 1
        val message = "Building docker images(%d/$imageCount):./containers/%s"

        println(message.format(1, "base"))
        buildImage("base", "${dockerImageNameHeader}base")

        sorted.forEachIndexed { index, language ->
            println(message.format(index + 2, language.dockerDirectory))
            buildImage(language.dockerDirectory, language.dockerImageName)
        }

        println("$imageCount images was successfully built.")
    }

    private fun buildImage(directory: String, imageName: String) {
        val command = "docker build -t $imageName $containersDirectory/$directory"
        val exitCode = ProcessBuilder(command)
                .executeByShell()
                .waitFor()
        if (exitCode != 0) {
            throw DockerException("The command '$command' returned a non-zero code: $exitCode")
        }
    }

    private class TopologicalSorting(val languages: List<Language>) {

        private val graph: List<List<Int>>
        private val result: MutableList<Int> = mutableListOf()
        private val visitFlags = Array(languages.size) { VisitEnum.Default }

        init {
            //build edges
            val mutableGraph = List(languages.size) {
                mutableListOf<Int>()
            }
            languages.forEachIndexed { index, language ->
                val dependsOn = language.dependsOn
                dependsOn ?: return@forEachIndexed
                mutableGraph[index].add(languages.indexOf(dependsOn))
            }
            graph = mutableGraph
        }

        fun sort(): List<Language> {
            (0..languages.lastIndex).forEach {
                if (visitFlags[it] == VisitEnum.Default) {
                    visit(it)
                }
            }

            return result.map { languages[it] }
        }

        private fun visit(n: Int) {
            if (visitFlags[n] == VisitEnum.Temporary) {
                throw IllegalStateException("Circular dependency.")
            }
            if (visitFlags[n] == VisitEnum.Default) {
                visitFlags[n] = VisitEnum.Temporary
                graph[n].forEach {
                    visit(it)
                }
                visitFlags[n] = VisitEnum.Permanent
                result.add(n)
            }
        }

        private enum class VisitEnum {
            Default,
            Temporary,
            Permanent
        }
    }
}