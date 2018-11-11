package app

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Random

@RestController
class Http(val kafkaProducer: KafkaProducer) {
    val log = LoggerFactory.getLogger(this::class.java)
    @GetMapping("/ping")
    fun ping(): String {
        val now = Instant.now().toEpochMilli()
        val msg = "[$now] Ping #${Random().nextInt(100)}"
        log.info("Producing $msg via kafka producer")
        kafkaProducer.send(msg, now)
        return msg
    }
}