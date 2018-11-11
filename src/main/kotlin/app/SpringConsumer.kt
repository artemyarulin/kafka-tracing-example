package app

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

// Not yet possible to inject tracing code https://github.com/reactor/reactor-kafka/pull/67
@Component
class SpringConsumer(val config: Config) {
    val log = LoggerFactory.getLogger(this::class.java)
    val receiver = {
        val props = hashMapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka,
            ConsumerConfig.CLIENT_ID_CONFIG to "spring-consumer",
            ConsumerConfig.GROUP_ID_CONFIG to "spring-consumer",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to LongDeserializer::class.java
        )
        log.info("Starting spring consumer")
        val rcv = KafkaReceiver
            .create(ReceiverOptions.create<String, String>(props).subscription(listOf(config.kafka)))
            .receive()
            .map {
                log.info("Processed $it")
                it.receiverOffset().acknowledge()
                it.value()
            }
            .publish() // Create hot publisher for multi subscribers
        rcv.connect()
        rcv
    }()
}

