package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.models.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverFilePath: String?, // путь к копии обложки во внутреннем хранилище
    val trackIdsJson: String,   // JSON со списком идентификаторов треков
    val trackCount: Int         // текущее количество треков в плейлисте
)

fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverUri = this.coverFilePath,
        trackCount = this.trackCount
    )
}