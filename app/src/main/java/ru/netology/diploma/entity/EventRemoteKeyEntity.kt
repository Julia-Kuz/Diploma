package ru.netology.diploma.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Suppress("unused", "unused")
@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Int,
) {
    enum class KeyType {
        AFTER, BEFORE
    }
}