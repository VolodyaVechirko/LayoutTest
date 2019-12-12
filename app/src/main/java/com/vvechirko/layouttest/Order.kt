package com.vvechirko.layouttest

data class Order(val id: String, var status: String = "new") {
    override fun toString(): String = id
    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = if (other is Order) other.id == id else false
}