package configuration.engine

import configuration.plugins.ActionId
import configuration.plugins.MissingPlugin
import configuration.plugins.StepAction
import it.czerwinski.kotlin.util.Either
import it.czerwinski.kotlin.util.Left
import it.czerwinski.kotlin.util.Right

data class Runners private constructor(
    val runners: Map<ActionId, Either<EngineError, StepAction>>
) {
    constructor(vararg runners: Pair<ActionId, StepAction>) : this(
        mapOf(*runners)
            .mapValues { Right(it.value) }
            .withDefault { Left(MissingPlugin(it)) }
    )
}
