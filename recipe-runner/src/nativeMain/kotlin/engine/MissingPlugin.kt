package engine

import plugins.ActionId

data class MissingPlugin(
    val pluginId: ActionId
) : EngineError