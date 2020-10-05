package com.hkm.consumerapp.tools

// Credit to Anushka Madusanka: https://stackoverflow.com/users/7365008/anushka-madusanka
// Source Code: https://stackoverflow.com/a/60750407
// Implemented as it is
class Event<out T>(private val content: T, val parameter: String = "") {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}