package com.porsche.pactkafka.consumer

interface VehicleProcessor {
    fun create(vehicle: Vehicle)
    fun update(vehicle: Vehicle)
    fun delete(id: Int)
}
