package com.porsche.pactkafka.producer

import au.com.dius.pact.provider.MessageAndMetadata
import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Consumer
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.loader.PactFolder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.messaging.Message
import org.springframework.util.concurrent.FailureCallback
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.util.concurrent.SuccessCallback
import java.util.concurrent.TimeUnit

@Provider("PactProducer")
@Consumer("PactConsumer")
@PactFolder("../pacts")
class ProducerPactTest {

    private val template = mockk<KafkaTemplate<String, Vehicle>>()
    private val producer = Producer(template)

    private val sendResult = object : ListenableFuture<SendResult<String, Vehicle>> {
        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            TODO("Not yet implemented")
        }

        override fun isCancelled(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isDone(): Boolean {
            TODO("Not yet implemented")
        }

        override fun get(): SendResult<String, Vehicle> {
            TODO("Not yet implemented")
        }

        override fun get(timeout: Long, unit: TimeUnit): SendResult<String, Vehicle> {
            TODO("Not yet implemented")
        }

        override fun addCallback(callback: ListenableFutureCallback<in SendResult<String, Vehicle>>) {
            TODO("Not yet implemented")
        }

        override fun addCallback(
            successCallback: SuccessCallback<in SendResult<String, Vehicle>>,
            failureCallback: FailureCallback
        ) {
            TODO("Not yet implemented")
        }

    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun testTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = MessageTestTarget()
    }

    @PactVerifyProvider("a vehicle create event")
    fun `provide a vehicle create event`(): MessageAndMetadata {

        val slot = slot<Message<Vehicle>>()
        every { template.send(capture(slot)) } answers { sendResult }

        producer.sendEvent("created", Vehicle(42, "blue", 4711.12))

        return slot.captured.toMessageAndMetadata()
    }

    @PactVerifyProvider("a vehicle update event")
    fun `provide a vehicle update event`(): MessageAndMetadata {

        val slot = slot<Message<Vehicle>>()
        every { template.send(capture(slot)) } answers { sendResult }

        producer.sendEvent("updated", Vehicle(42, "blue", 4711.12))

        return slot.captured.toMessageAndMetadata()
    }

    @PactVerifyProvider("a vehicle delete event")
    fun `provide a vehicle delete event`(): MessageAndMetadata {

        val slot = slot<Message<Vehicle>>()
        every { template.send(capture(slot)) } answers { sendResult }

        producer.sendEvent("deleted", Vehicle(42, "blue", 4711.12))

        return slot.captured.toMessageAndMetadata()
    }

    private fun <T> Message<T>.toMessageAndMetadata(): MessageAndMetadata =
        MessageAndMetadata(objectMapper.writeValueAsBytes(payload), headers)

    companion object {
        private val objectMapper = ObjectMapper().registerKotlinModule()
    }
}

