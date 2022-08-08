package com.hlabs.rbme.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Entry not found")
class NotFoundException : RuntimeException()