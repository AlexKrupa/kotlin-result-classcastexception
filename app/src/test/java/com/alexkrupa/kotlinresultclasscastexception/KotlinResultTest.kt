package com.alexkrupa.kotlinresultclasscastexception

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetValue {

    suspend operator fun invoke(): Result<Unit> {
        delay(0)
        return Result.failure(Exception("Bleh"))
    }
}

class ViewModel(private val getValue: GetValue) : androidx.lifecycle.ViewModel() {

    fun onClickFailure() {
        viewModelScope.launch {
            getValue().onSuccess { Unit }
        }
    }

    fun onClickFine() {
        viewModelScope.launch {
            getValue.invoke().onSuccess { Unit }
        }
    }
}

class KotlinResultTest {

    private val repository = GetValue()
    private val viewModel = ViewModel(repository)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

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