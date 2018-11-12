package app

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// Example of complex topology when multiple topics are inputs and one is an output
// For tracing Kafka Streams just wrap it with KafkaStreamsTracing
@Component
class KafkaStreamsManyToOne(val config: Config, val tracer: Tracer) {
    val log = LoggerFactory.getLogger(this::class.java)
    val stream = {
        val props = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "kafka-streams-many-to-one",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka
        ).toProperties()
        log.info("Starting stream many to one")
        val builder = StreamsBuilder().apply {
            stream(
                listOf(config.topic, config.topic + "_a", config.topic + "_b", config.topic + "_bb"),
                Consumed.with(Serdes.String(), Serdes.Long())
            )
                .mapValues { k, v ->
                    log.info("[* to 1] Processing $k and $v")
                    Thread.sleep(1000)
                    v + 4
                }
                .to(config.topic + "_c", Produced.with(Serdes.String(), Serdes.Long()))
        }
        tracer.kafkaStreamsTracer("stream-c").kafkaStreams(builder.build(), props).start()
    }()
}