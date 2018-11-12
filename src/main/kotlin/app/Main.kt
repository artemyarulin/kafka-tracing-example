package app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Config {
    // Kafka Server
    val kafka = "localhost:9092"
    // Topic prefix, ensure Kafka allows to create new topics automatically
    val topic = "example"
    // Zipkin server
    val zipkin = "http://127.0.0.1:9411/api/v2/spans"
}

fun main(args: Array<String>) {
    SpringApplication.run(Config::class.java, *args)
}