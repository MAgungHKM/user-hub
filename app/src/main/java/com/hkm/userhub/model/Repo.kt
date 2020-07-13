package com.hkm.userhub.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Repo(
    val name: String = "",
    val description: String = "",
    val repoLink: String = "",
    val stargazers: String = ""
) : Parcelable