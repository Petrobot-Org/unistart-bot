package ru.spbstu.application.auth.entities

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PhoneNumberTest {

    @Test
    fun test() {
        assertEquals(PhoneNumber.valueOf("000")!!.value, "000")
        assertEquals(PhoneNumber.valueOf("000a"), null)
    }
}
