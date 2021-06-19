package com.porsche.pactkafka.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class Consumer(private val vehicleProcessor: VehicleProcessor) {

    @KafkaListener(groupId = "pactkafka-vehicles", topics = ["vehicles"])
    fun carEvent(@Header("EventType") eventType: String, vehicle: Vehicle) {
        when (eventType) {
            "CREATED" -> vehicleProcessor.create(vehicle)
            "UPDATED" -> vehicleProcessor.update(vehicle)
            "DELETED" -> vehicleProcessor.delete(vehicle.id)
        }
    }
}
