package com.warbler.utilities

class ClickListener<T>(val clickListener: (item: T) -> Unit) {
    fun onClick(item: T) = clickListener(item)
}

interface ClickListenerInterface<T> {
    fun onClick(item: T)

    fun delete(item: T)
}
