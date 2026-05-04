package com.example.counter

interface UseCase<in Params> {
    suspend fun execute(params: Params)
}

suspend fun UseCase<Unit>.execute() = execute(Unit)
