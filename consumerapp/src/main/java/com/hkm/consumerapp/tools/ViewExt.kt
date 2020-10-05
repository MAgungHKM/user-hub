package com.hkm.consumerapp.tools

import android.view.View

// Credit to Charles Madere: https://stackoverflow.com/users/823952/charles-madere
// Source Code: https://stackoverflow.com/a/52415792
// Implemented as it is
fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}