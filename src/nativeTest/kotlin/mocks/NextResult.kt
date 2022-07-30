package mocks

import engine.EngineError

sealed interface NextResult {
    data class Success<T>(
        val result: T
    ) : NextResult

    data class Failure(val error: EngineError) : NextResult
}