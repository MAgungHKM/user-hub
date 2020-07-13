package com.hkm.userhub.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val avatar: String = "",
    val name: String = "",
    val username: String = "",
    val location: String = "",
    val company: String = "",
    val followersCount: String = ""
) : Parcelable