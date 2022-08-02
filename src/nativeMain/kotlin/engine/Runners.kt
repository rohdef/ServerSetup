package engine

import arrow.core.Either
import plugins.ActionId
import plugins.MissingPlugin
import plugins.StepAction

data class Runners private constructor(
    val runners: Map<ActionId, Either<EngineError, StepAction>>
) {
    constructor(vararg runners: Pair<ActionId, StepAction>) : this(
        mapOf(*runners)
            .mapValues { Either.Right(it.value) }
            .withDefault { Either.Left(MissingPlugin(it)) }
    )
}
