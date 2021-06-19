package com.porsche.pactkafka.consumer

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.newJsonObject
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactDirectory
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "PactProducer", providerType = ProviderType.ASYNCH)
@PactDirectory("../pacts")
class ConsumerPactTest {

    private val vehicleProcessor = mockk<VehicleProcessor>(relaxUnitFun = true)

    private var consumer = Consumer(vehicleProcessor)


    @Pact(consumer = "PactConsumer")
    fun `setup create vehicle event`(builder: MessagePactBuilder): MessagePact {
        return builder
            .expectsToReceive("a vehicle create event")
            .withContent(newJsonObject {
                numberType("id", 1)
                stringType("color", "red")
                numberType("price", 12345.67)
            })
            .withMetadata(
                mapOf("EventType" to "CREATED")
            )
            .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "setup create vehicle event")
    fun `should process created vehicle event`(messages: List<Message>) {
        val (eventType, vehicle) = eventWithVehicle(messages)

        consumer.carEvent(eventType, vehicle)

        verify { vehicleProcessor.create(vehicle) }
    }

    @Pact(consumer = "PactConsumer")
    fun `setup update vehicle event`(builder: MessagePactBuilder): MessagePact {
        return builder
            .expectsToReceive("a vehicle update event")
            .withContent(newJsonObject {
                numberType("id", 1)
                stringType("color", "green")
                numberType("price", 12345.67)
            })
            .withMetadata(
                mapOf("EventType" to "UPDATED")
            )
            .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "setup update vehicle event")
    fun `should process updated vehicle event`(messages: List<Message>) {
        val (eventType, vehicle) = eventWithVehicle(messages)

        consumer.carEvent(eventType, vehicle)

        verify { vehicleProcessor.update(vehicle) }
    }

    @Pact(consumer = "PactConsumer")
    fun `setup delete vehicle event`(builder: MessagePactBuilder): MessagePact {
        return builder
            .expectsToReceive("a vehicle delete event")
            .withContent(newJsonObject {
                numberType("id", 1)
            })
            .withMetadata(
                mapOf("EventType" to "DELETED")
            )
            .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "setup delete vehicle event")
    fun `should process deleted vehicle event`(messages: List<Message>) {
        val (eventType, vehicle) = eventWithVehicle(messages)

        consumer.carEvent(eventType, vehicle)

        verify { vehicleProcessor.delete(vehicle.id) }
    }

    private fun eventWithVehicle(messages: List<Message>): Pair<String, Vehicle> {
        val message = messages[0]
        val eventType = message.metadata["EventType"] as String
        val vehicle = objectMapper.readValue(message.contentsAsString(), Vehicle::class.java)
        return Pair(eventType, vehicle)
    }

    companion object {
        private val objectMapper = ObjectMapper().registerKotlinModule()
    }
}
