package app

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// Example of simple kafka streams topology of mapping items from one topic to another
// For tracing Kafka Streams just wrap it with KafkaStreamsTracing
@Component
class KafkaStreamsOneToOne(val config: Config, val tracer: Tracer) {
    val log = LoggerFactory.getLogger(this::class.java)
    val stream = {
        val props = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "kafka-streams-one-to-one",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka
        ).toProperties()
        log.info("Starting stream one to one")
        val builder = StreamsBuilder().apply {
            stream(config.topic, Consumed.with(Serdes.String(), Serdes.Long()))
                .mapValues { k, v ->
                    log.info("[1 to 1] Processing $k and $v")
                    Thread.sleep(1000)
                    v + 1
                }
                .to(config.topic + "_a", Produced.with(Serdes.String(), Serdes.Long()))
        }
        tracer.kafkaStreamsTracer("stream-a").kafkaStreams(builder.build(), props).start()
    }()
}