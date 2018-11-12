package app

import brave.Tracing
import brave.kafka.clients.KafkaTracing
import brave.kafka.streams.KafkaStreamsTracing
import org.springframework.stereotype.Component
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.okhttp3.OkHttpSender

// We are using Spring Boot in this example which has integration with Zipkin
// but for the example purposes we configure everything manually
@Component
class Tracer(val config: Config) {
    fun kafkaTracer(name: String): KafkaTracing {
        val okHttpSender = OkHttpSender.create(config.zipkin)
        val reporter = AsyncReporter.create(okHttpSender)
        val tracing = Tracing
            .newBuilder()
            .localServiceName(name)
            .spanReporter(reporter)
            .build()
        return KafkaTracing.create(tracing)
    }

    fun kafkaStreamsTracer(name: String): KafkaStreamsTracing {
        val okHttpSender = OkHttpSender.create(config.zipkin)
        val reporter = AsyncReporter.create(okHttpSender)
        val tracing = Tracing
            .newBuilder()
            .localServiceName(name)
            .spanReporter(reporter)
            .build()
        return KafkaStreamsTracing.create(tracing)
    }
}
