package com.example.testapplt.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListItem (
    val id: String,
    val title: String,
    val text: String,
    val image: String?,
    val sort: Int,
    val date: String,
    ): Parcelable