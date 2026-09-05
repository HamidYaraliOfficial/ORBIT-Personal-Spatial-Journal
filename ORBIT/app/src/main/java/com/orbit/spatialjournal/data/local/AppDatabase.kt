package com.orbit.spatialjournal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orbit.spatialjournal.data.local.dao.*
import com.orbit.spatialjournal.data.local.entity.*

@Database(
    entities = [
        MemoryEntity::class, MemoryFtsEntity::class, PlaceEntity::class, TripEntity::class,
        TagEntity::class, RelationshipEntity::class, JournalEntryEntity::class, VisitEntity::class,
        CollectionEntity::class, CollectionMemberEntity::class, ReminderEntity::class,
        MapAnnotationEntity::class, AttachmentEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun placeDao(): PlaceDao
    abstract fun tripDao(): TripDao
    abstract fun tagDao(): TagDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun journalDao(): JournalDao
    abstract fun visitDao(): VisitDao
    abstract fun collectionDao(): CollectionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun mapAnnotationDao(): MapAnnotationDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        /**
         * Placeholder for the next schema bump. Every future migration must be added here
         * with an explicit, tested Migration(from, to) — Room's fallbackToDestructiveMigration
         * is intentionally NOT used in release builds so a user's memories are never silently
         * wiped by an app update. See DatabaseMigrationTest.
         */
        val MIGRATIONS = arrayOf<androidx.room.migration.Migration>()
    }
}
