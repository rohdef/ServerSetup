package engine

import arrow.core.Either
import dk.rohdef.plugins.ActionId
import dk.rohdef.plugins.StepAction

data class Runners private constructor(
    val runners: Map<ActionId, Either<EngineError, StepAction>>
) {
    constructor(vararg runners: StepAction) : this(listOf(*runners))

    constructor(runners: List<StepAction>) : this(
        runners
            .associateBy { it.actionId }
            .mapValues { Either.Right(it.value) }
            .withDefault { Either.Left(MissingPlugin(it)) }
    )
}