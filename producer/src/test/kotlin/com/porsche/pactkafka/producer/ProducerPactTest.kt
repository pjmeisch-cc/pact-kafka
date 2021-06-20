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
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message

@Provider("PactProducer")
@Consumer("PactConsumer")
@PactFolder("../pacts")
class ProducerPactTest {

    private val template = mockk<KafkaTemplate<String, Vehicle>>()
    private val producer = Producer(template)

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
    fun `provide a vehicle create event`() =
        producer.buildMessage("created", Vehicle(42, "blue", 4711.12)).toMessageAndMetadata()

    @PactVerifyProvider("a vehicle update event")
    fun `provide a vehicle update event`() =
        producer.buildMessage("updated", Vehicle(42, "blue", 4711.12)).toMessageAndMetadata()

    @PactVerifyProvider("a vehicle delete event")
    fun `provide a vehicle delete event`() =
        producer.buildMessage("deleted", Vehicle(42, "blue", 4711.12)).toMessageAndMetadata()

    private fun <T> Message<T>.toMessageAndMetadata(): MessageAndMetadata =
        MessageAndMetadata(objectMapper.writeValueAsBytes(payload), headers)

    companion object {
        private val objectMapper = ObjectMapper().registerKotlinModule()
    }
}

