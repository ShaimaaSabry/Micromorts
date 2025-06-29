package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActionUnitException
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class ActionUnitTest {
    @Nested
    inner class Parse {
        @Test
        fun `should parse valid unit`() {
            // when
            val unit = ActionUnit.parse("mile")

            // then
            assert(unit == ActionUnit.MILE)
        }

        @Test
        fun `should throw exception for invalid unit`() {
            // when
            val exception =
                runCatching {
                    ActionUnit.parse("invalid")
                }.exceptionOrNull()

            // then
            assert(exception is ActionUnitException)
            assert(exception?.message == "Invalid unrecognized unit: invalid")
        }
    }
}
