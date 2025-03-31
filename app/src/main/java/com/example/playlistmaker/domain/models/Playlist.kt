package com.example.playlistmaker.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverUri: String?,
    val trackCount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(coverUri)
        parcel.writeInt(trackCount)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel) = Playlist(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Playlist?>(size)
    }
}
