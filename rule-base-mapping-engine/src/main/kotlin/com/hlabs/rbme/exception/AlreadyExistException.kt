package com.hlabs.rbme.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.FOUND, reason = "Entry already exists")
class AlreadyExistException : RuntimeException()