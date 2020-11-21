package com.alexkrupa.kotlinresultclasscastexception

import kotlinx.coroutines.*
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class GetValue {

    @Suppress("RedundantSuspendModifier")
    suspend operator fun invoke(): Result<Unit> {
        return Result.success(Unit)
    }
}

class ViewModel(private val getValue: GetValue) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    fun onClickFailure() {
        scope.launch { getValue() }
    }

    fun onClickFine() {
        scope.launch { getValue.invoke() }
    }
}

class KotlinResultTest {

    private val repository = GetValue()
    private val viewModel = ViewModel(repository)

    @Test
    fun verifyErrorOccurs() = runBlocking {
        try {
            viewModel.onClickFailure()
            fail("Expected VerifyError thrown.")
        } catch (error: VerifyError) {
            assertTrue(error.message!!.startsWith("Bad type on operand stack"))
        }
    }

    @Test
    fun worksJustFine() = runBlocking {
        try {
            viewModel.onClickFine()
        } catch (error: Error) {
            fail("Expected no errors but got: $error")
        }
    }
}