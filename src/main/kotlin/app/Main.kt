package app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Config {
    val kafka = "localhost:9092"
    val topic = "example"
}

fun main(args: Array<String>) {
    SpringApplication.run(Config::class.java, *args)
}