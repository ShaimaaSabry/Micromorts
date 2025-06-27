package com.miradx.shaimaa.micromorts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MicromortsApplication

fun main(args: Array<String>) {
	runApplication<MicromortsApplication>(*args)
}
