package com.hkm.userhub.entitiy

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class User(
    var avatar: String = "",
    var name: String = "",
    @PrimaryKey var username: String = "",
    var location: String = "",
    var company: String = "",
    var followersCount: String = ""
) : Parcelable, RealmObject()