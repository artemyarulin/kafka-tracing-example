package app

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

// For tracing simple Kafka Consumers just wrap it with KafkaTracing
@Component
class KafkaConsumer(val config: Config, val tracer: Tracer) {
    val log = LoggerFactory.getLogger(this::class.java)
    val consumer = {
        val consumer = tracer.kafkaTracer("consumer-a").consumer(
            KafkaConsumer<String, Long>(
                mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka,
                    ConsumerConfig.GROUP_ID_CONFIG to "kafka_consumer",
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer().javaClass.name,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to LongDeserializer().javaClass.name
                ).toProperties()
            )
        )
        log.info("Starting kafka consumer")
        consumer.apply { subscribe(listOf(config.topic)) }
    }()
    val poller =
        Mono.fromCallable {
            while (true) {
                val records = consumer.poll(Duration.ofSeconds(1))
                records.forEach {
                    log.info("Consumed ${it.key()}:${it.value()}")
                }
            }
        }.subscribeOn(Schedulers.newSingle("kafkaConsumer")).subscribe()
}