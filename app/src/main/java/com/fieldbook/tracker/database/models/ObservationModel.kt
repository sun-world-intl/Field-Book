package com.fieldbook.tracker.database.models

import android.util.Log
import com.fieldbook.tracker.database.Row
import com.fieldbook.tracker.database.dao.ObservationVariableDao
import java.lang.reflect.Field
import java.util.Locale

data class ObservationModel(val map: Row) {
        val internal_id_observation: Int by map
        val observation_unit_id: String by map
        val observation_variable_db_id: Int by map
        val observation_variable_field_book_format: String? by map
        val observation_variable_name: String? by map
        var value: String = if ("value" in map) (map["value"] ?: "NA").toString()
                        else if ("observation_variable_db_id" in map.keys) ObservationVariableDao.getTraitById(observation_variable_db_id)?.defaultValue ?: "NA"
                        else "NA"
        val observation_time_stamp: String? by map
        val collector: String? by map
        val geo_coordinates: String? = if (map.containsKey("geoCoordinates")) map["geoCoordinates"]?.toString() else null
        val study_id: String = (map["study_id"] ?: -1).toString()
        val last_synced_time: String by map
        val additional_info: String? by map
        var rep: String = if (map.containsKey("rep")) map["rep"]?.toString() ?: "1" else "1"

        //used during file migration when updating photo/audio values to uris
        fun createMap() = mutableMapOf<String, Any?>(
                "internal_id_observation" to internal_id_observation,
                "value" to value,
        )

        companion object {
                fun createInstance(variableDbId: Int, traitName: String) = ObservationModel(mapOf(
                        "observation_variable_db_id" to variableDbId,
                        "observation_variable_name" to traitName
                ))
        }

        fun showNonNullAttributesDialog(): MutableMap<String, Any> {
                val nonNullAttributes = mutableMapOf<String, Any>()

                // get the "map" property
                val mapProperty = ObservationModel::class.java.declaredFields.firstOrNull { it.name == "map" }
                mapProperty?.isAccessible = true
                try {
                        if (mapProperty != null) {
                                mapProperty.isAccessible = true
                                val mapValue = mapProperty.get(this)

                                if (mapValue is Map<*, *>) {
                                        // Iterate through the attributes
                                        for ((key, value) in mapValue) {
                                                if (
                                                        (value != null) &&
                                                        value.toString().isNotEmpty() &&
                                                        (value.toString().trim() != "")
                                                ) {
                                                        nonNullAttributes[formattedName(key.toString())] = value
                                                }
                                        }
                                }
                        }
                } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                }

                return nonNullAttributes
        }

        private fun formattedName(attributeName : String) : String {
                val parts = attributeName.split("_").mapIndexed { _, part ->
                        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
                return parts.joinToString(" ")
        }
}