package com.androiddevs.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Source

/**
 * Contains type converters for Room to handle custom data types.
 * Room uses these methods to convert complex types to and from a format it can store in the database.
 */
class Converters {

    /** Converts a Source object to its name as a String for database storage. */
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    /** Converts a String (name) back into a Source object when reading from the database. */
    @TypeConverter
    fun toSource(name: String): Source {
        // Note: The id of the source is reconstructed from its name.
        return Source(name, name)
    }

}