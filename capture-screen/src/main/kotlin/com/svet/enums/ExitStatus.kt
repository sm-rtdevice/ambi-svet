package com.svet.enums

enum class ExitStatus(val status: Int) {
    OK(0),
    MISSING_PARAM_BY_INDEX(1),
    INVALID_PARAM(2),
    NOT_SUPPORTED(3)
}
