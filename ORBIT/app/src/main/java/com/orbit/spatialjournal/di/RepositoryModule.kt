package com.orbit.spatialjournal.di

import com.orbit.spatialjournal.data.repository.*
import com.orbit.spatialjournal.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton abstract fun bindMemoryRepository(impl: MemoryRepositoryImpl): MemoryRepository
    @Binds @Singleton abstract fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository
    @Binds @Singleton abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository
    @Binds @Singleton abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository
    @Binds @Singleton abstract fun bindRelationshipRepository(impl: RelationshipRepositoryImpl): RelationshipRepository
    @Binds @Singleton abstract fun bindVisitRepository(impl: VisitRepositoryImpl): VisitRepository
    @Binds @Singleton abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository
    @Binds @Singleton abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
    @Binds @Singleton abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository
    @Binds @Singleton abstract fun bindExportRepository(impl: ExportRepositoryImpl): ExportRepository
}
