import arrow.core.continuations.either
import dk.rohdef.rfpath.utility.PathUtility

suspend fun generateRunScript(pathUtility: PathUtility) = either {
    val applicationDirectory = pathUtility.applicationDirectory().bind()
    val scriptFile = applicationDirectory.newFile("gourmet.py").bind()

    scriptFile.write(
        """
        #!/bin/env python3

        import json
        import sys
        import yaml

        from subprocess import run

        def main():
            pathToGourmet = "./recipe-runner.kexe"
            argsToPass = sys.argv[1:-1]

            if len(sys.argv) > 1:
                pathToRecipe = sys.argv[-1:][0]
                recipeJson = parseRecipe(pathToRecipe)
                recipe = [recipeJson]
            else:
                recipe = []

            command = [pathToGourmet] + argsToPass + recipe

            run(command)

        def parseRecipe(pathToRecipe):
            with open(pathToRecipe, "r") as file:
                recipe = yaml.safe_load(file)

            return json.dumps(recipe)

        if __name__ == "__main__":
            main()
        """.trimIndent()
    )
}