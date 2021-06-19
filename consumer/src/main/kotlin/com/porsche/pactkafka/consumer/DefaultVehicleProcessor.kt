package com.porsche.pactkafka.consumer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultVehicleProcessor : VehicleProcessor {
    override fun create(vehicle: Vehicle) {
        LOGGER.info("CREATED: $vehicle")
    }

    override fun update(vehicle: Vehicle) {
        LOGGER.info("UPDATED: $vehicle")
    }

    override fun delete(id: Int) {
        LOGGER.info("DELETED: $id")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultVehicleProcessor::class.java)
    }
}
