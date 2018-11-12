package app

import brave.Tracer
import brave.Tracing
import brave.kafka.clients.KafkaTracing
import brave.sampler.Sampler
import org.springframework.stereotype.Component
import zipkin2.codec.Encoding
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.okhttp3.OkHttpSender

@Component
class Tracerrr {
    val trace = {
        val okHttpSender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans")
        val reporter = AsyncReporter.create(okHttpSender)
        val tracing = Tracing
            .newBuilder()
            .localServiceName("Kafkas")
            .spanReporter(reporter)
            .build()
        KafkaTracing.newBuilder(tracing)
            .remoteServiceName("my-broker")
            .build()
    }()
}
