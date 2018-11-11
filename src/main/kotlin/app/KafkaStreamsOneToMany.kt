package app

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KafkaStreamsOneToMany(val config: Config) {
    val log = LoggerFactory.getLogger(this::class.java)
    val stream = {
        val props = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "kafka-streams-one-to-many",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to config.kafka
        ).toProperties()
        log.info("Starting stream one to many")
        val builder = StreamsBuilder().apply {
            stream(config.topic, Consumed.with(Serdes.String(), Serdes.Long()))
                .mapValues { k, v ->
                    log.info("[1-to-*] 1 x Processing $k and $v")
                    v + 2
                }
                .through(config.topic + "_2", Produced.with(Serdes.String(), Serdes.Long()))
                .mapValues { k, v ->
                    log.info("[1-to-*] 2 x Processing $k and $v")
                    v + 3
                }
                .to(config.topic + "_3", Produced.with(Serdes.String(), Serdes.Long()))
        }
        KafkaStreams(builder.build(), props).start()
    }()
}