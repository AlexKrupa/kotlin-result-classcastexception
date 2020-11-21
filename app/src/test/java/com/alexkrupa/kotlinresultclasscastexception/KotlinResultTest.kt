package com.alexkrupa.kotlinresultclasscastexception

import kotlinx.coroutines.*
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test



interface Repository {
    suspend fun getValue(): Result<String>
}

class RepositoryImpl : Repository {

    override suspend fun getValue(): Result<String> {
        delay(0)
        return Result.success("Value")
    }
}

class ViewModel(private val repository: Repository) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    fun onClick() {
        scope.launch {
            repository.getValue()
                .onSuccess { Unit }
        }
    }
}

class KotlinResultTest {

    private val repository = RepositoryImpl()
    private val viewModel = ViewModel(repository)

    @Test
    fun classCastExceptionOccurs() = runBlocking {
        try {
            viewModel.onClick()
            fail("Expected ClassCastException thrown.")
        } catch (exception: ClassCastException) {
            assertTrue(
                exception.message!!
                    .startsWith("class kotlin.Result cannot be cast to class kotlin.Unit")
            )
        }
    }
}