package com.hkm.consumerapp.entitiy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Repo(
    var name: String = "",
    var description: String = "",
    var repoLink: String = "",
    var stargazers: String = ""
) : Parcelable