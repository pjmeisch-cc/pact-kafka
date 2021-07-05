package com.porsche.pactkafka.producer

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/vehicles")
class Producer(private val template: KafkaTemplate<String, Vehicle>) {

    private var vehicles = mutableMapOf<Int, Vehicle>()

    @GetMapping
    fun getAll() = vehicles.values.toList()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int) = vehicles.getOrElse(id) {
        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping()
    fun save(@RequestBody vehicle: Vehicle) {
        val id = vehicle.id
        val oldVehicle = vehicles.put(id, vehicle)
        if (oldVehicle != null) {
            sendEvent("updated", vehicle)
            LOGGER.info("updated vehicle $vehicle")
        } else {
            sendEvent("created", vehicle)
            LOGGER.info("created vehicle $vehicle")
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        vehicles.remove(id)?.let { vehicle ->
            sendEvent("deleted", vehicle)
            LOGGER.info("deleted vehicle $vehicle")
        }
    }

    fun sendEvent(eventType: String, vehicle: Vehicle) {
        template.send(
            MessageBuilder
                .withPayload(vehicle)
                .setHeader(KafkaHeaders.TOPIC, "vehicles")
                .setHeader("EventType", eventType.uppercase())
                .build()
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Producer::class.java)
    }
}
