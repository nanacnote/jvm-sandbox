package com.hlabs.rbme.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/ui")
class UiController {

    @GetMapping
    fun index(): String {
        return "home"
    }

    @GetMapping("/upload")
    fun upload(): String {
        return "upload"
    }

    @GetMapping("/pending")
    fun pending(): String {
        return "pending"
    }

    @GetMapping("/complete")
    fun complete(): String {
        return "complete"
    }
}
