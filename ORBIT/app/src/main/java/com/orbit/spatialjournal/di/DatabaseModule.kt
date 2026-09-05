package com.orbit.spatialjournal.di

import android.content.Context
import androidx.room.Room
import com.orbit.spatialjournal.core.util.Constants
import com.orbit.spatialjournal.data.local.AppDatabase
import com.orbit.spatialjournal.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .addMigrations(*AppDatabase.MIGRATIONS)
            // Destructive fallback is intentionally NOT enabled here (see AppDatabase docs) —
            // every schema change ships with a real, tested Migration instead.
            .build()

    @Provides fun provideMemoryDao(db: AppDatabase): MemoryDao = db.memoryDao()
    @Provides fun providePlaceDao(db: AppDatabase): PlaceDao = db.placeDao()
    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
    @Provides fun provideRelationshipDao(db: AppDatabase): RelationshipDao = db.relationshipDao()
    @Provides fun provideJournalDao(db: AppDatabase): JournalDao = db.journalDao()
    @Provides fun provideVisitDao(db: AppDatabase): VisitDao = db.visitDao()
    @Provides fun provideCollectionDao(db: AppDatabase): CollectionDao = db.collectionDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideMapAnnotationDao(db: AppDatabase): MapAnnotationDao = db.mapAnnotationDao()
    @Provides fun provideAttachmentDao(db: AppDatabase): AttachmentDao = db.attachmentDao()
}
