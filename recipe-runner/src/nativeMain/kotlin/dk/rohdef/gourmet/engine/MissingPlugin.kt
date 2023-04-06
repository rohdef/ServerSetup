package dk.rohdef.gourmet.engine

import dk.rohdef.plugins.ActionId
import engine.EngineError

data class MissingPlugin(
    val pluginId: ActionId
) : EngineError