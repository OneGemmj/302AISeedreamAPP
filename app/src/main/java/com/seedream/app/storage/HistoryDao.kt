package com.seedream.app.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query(
        """
        SELECT * FROM history_images
        WHERE (:keyword = '' OR prompt LIKE '%' || :keyword || '%' COLLATE NOCASE)
        ORDER BY timestamp DESC
        LIMIT :limit
        """
    )
    suspend fun page(limit: Int, keyword: String): List<HistoryEntity>

    @Query("SELECT COUNT(*) FROM history_images")
    suspend fun count(): Int

    @Query(
        """
        SELECT COUNT(*) FROM history_images
        WHERE (:keyword = '' OR prompt LIKE '%' || :keyword || '%' COLLATE NOCASE)
        """
    )
    suspend fun countMatching(keyword: String): Int

    @Insert
    suspend fun insert(item: HistoryEntity): Long

    @Query("DELETE FROM history_images WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history_images")
    suspend fun clear()

    @Query("SELECT * FROM history_images")
    suspend fun allOnce(): List<HistoryEntity>

    @Query("SELECT * FROM history_images WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): HistoryEntity?
}
