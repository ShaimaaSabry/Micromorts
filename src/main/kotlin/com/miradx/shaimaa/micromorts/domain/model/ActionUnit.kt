package com.miradx.shaimaa.micromorts.domain.model

import com.miradx.shaimaa.micromorts.domain.exception.ActionException

enum class ActionUnit {
    MILE,
    FLOOR,
    MINUTE,
    QUANTITY;

    companion object {
        fun parseUnit(value: String): ActionUnit {
            return kotlin.runCatching {
                enumValueOf<ActionUnit>(value.uppercase())
            }.getOrElse {
                throw ActionException.invalidActionUnit(value)
            }
        }
    }
}
