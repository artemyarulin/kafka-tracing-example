package app

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KafkaStreamsManyToOne(val config: Config) {
    val log = LoggerFactory.getLogger(this::class.java)
    val stream = {
        val props = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "kafka-streams-one-to-many",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka
        ).toProperties()
        log.info("Starting stream many to one")
        val builder = StreamsBuilder().apply {
            stream(
                listOf(config.topic, config.topic + "_2", config.topic + "_3"),
                Consumed.with(Serdes.String(), Serdes.Long())
            )
                .mapValues { k, v ->
                    log.info("Processing $k and $v")
                    v + 4
                }
                .to(config.topic + "_4")
        }
        KafkaStreams(builder.build(), props).start()
    }()
}