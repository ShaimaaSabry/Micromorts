package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exceptions.ActionUnitException

enum class ActionUnit {
    MILE,
    FLOOR,
    MINUTE,
    QUANTITY,
    ;

    companion object {
        fun parse(value: String): ActionUnit =
            kotlin
                .runCatching {
                    enumValueOf<ActionUnit>(value.uppercase())
                }.getOrElse {
                    throw ActionUnitException.invalidUnit(value)
                }
    }
}
