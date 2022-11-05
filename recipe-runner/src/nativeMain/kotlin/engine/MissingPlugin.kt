package engine

import dk.rohdef.plugins.ActionId

data class MissingPlugin(
    val pluginId: ActionId
) : EngineError