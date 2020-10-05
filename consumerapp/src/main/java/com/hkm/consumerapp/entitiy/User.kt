package com.hkm.consumerapp.entitiy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class User(
    var avatar: String = "",
    var name: String = "",
    var username: String = "",
    var location: String = "",
    var company: String = "",
    var followers: String = ""
) : Parcelable