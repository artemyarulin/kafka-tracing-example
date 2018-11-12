package app

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// For tracing simple Kafka Producer just wrap it with KafkaTracing
@Component
class KafkaProducer(val config: Config, val tracer: Tracer) {
    val log = LoggerFactory.getLogger(this::class.java)
    val producer = {
        log.info("Starting kafka producer")
        tracer.kafkaTracer("producer-a").producer(
            KafkaProducer<String, Long>(
                mapOf(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer().javaClass.name,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to LongSerializer().javaClass.name
                ).toProperties()
            )
        )
    }()

    fun send(key: String, value: Long) {
        log.info("Sending $key:$value")
        producer.send(ProducerRecord<String, Long>(config.topic, key, value))
    }
}
