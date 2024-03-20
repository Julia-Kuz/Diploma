package ru.netology.diploma.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.PostEntity


@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAll(): Flow<List<EventEntity>>  // Правильный импорт д.б. - kotlinx.coroutines.flow.Flow !!!

    @Query("SELECT * FROM EventEntity ORDER BY id DESC") //пагинация
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Query("DELETE FROM EventEntity")
    fun removeAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("UPDATE EventEntity SET isPlaying = :isPlaying")
    fun updatePlayerEvent (isPlaying: Boolean)

    @Query("UPDATE EventEntity SET isPlaying = :isPlaying WHERE id = :eventId")
    suspend fun updateIsPlayingEvent (eventId: Int, isPlaying: Boolean)

    @Query("""
        UPDATE EventEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun likeById(id: Int)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)

}