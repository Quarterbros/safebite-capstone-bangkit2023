package com.example.safebitecapstone.dummyData

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Alergen(
    val name: String,
    val desc: String,
    val photo: Int,
) : Parcelable

