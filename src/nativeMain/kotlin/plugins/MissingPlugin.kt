package plugins

import engine.EngineError

data class MissingPlugin(
    val pluginId: ActionId
) : EngineError