package com.orbit.spatialjournal.di

import com.orbit.spatialjournal.domain.repository.MemoryRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Lets non-injectable classes (Glance widgets, the geofence receiver's fast-path helpers) pull
 * singletons out of the Hilt graph without needing @AndroidEntryPoint. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetDataEntryPoint {
    fun memoryRepository(): MemoryRepository
}
