package com.example.counter

interface UseCase<in ExecuteParams, in WithParams> {
    fun with(params: WithParams): Executor<ExecuteParams>

    interface Executor<in ExecuteParams> {
        suspend fun execute(params: ExecuteParams)
    }
}

suspend fun UseCase.Executor<Unit>.execute() = execute(Unit)
