package com.hkm.userhub.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var avatar: String = "",
    var name: String = "",
    var username: String = "",
    var location: String = "",
    var company: String = "",
    var followersCount: String = ""
) : Parcelable