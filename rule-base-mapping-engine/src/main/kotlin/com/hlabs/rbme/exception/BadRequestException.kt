package com.hlabs.rbme.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Entry is invalid")
class BadRequestException : RuntimeException()