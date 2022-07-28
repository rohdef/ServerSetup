package configuration.plugins

import configuration.engine.EngineError

data class MissingPlugin(
    val pluginId: ActionId
) : EngineError