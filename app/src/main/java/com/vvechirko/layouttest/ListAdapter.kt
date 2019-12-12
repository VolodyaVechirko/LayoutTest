package com.vvechirko.layouttest

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

abstract class ListAdapter<T>(val columns: List<LinearLayout>) {

    val spanCount = 2
    val columnItems: List<MutableList<T>> = listOf(mutableListOf(), mutableListOf())

    abstract fun onCreateHolder(parent: ViewGroup): View
    abstract fun onBindHolder(view: View, item: T)

    fun setItems(list: List<T>) {
        // split orders for columns
        list.forEachIndexed { index, item ->
            val span = index % spanCount
            columnItems[span].add(item)
        }

        // populate containers of columns
        for (i in 0 until spanCount) {
            columns[i].removeAllViews()
            columnItems[i].forEach { item ->
                val v = onCreateHolder(columns[i])
                onBindHolder(v, item)
                columns[i].addView(v)
            }
        }
    }

    fun addItems(list: List<T>) {
        // split orders for columns
        list.forEachIndexed { index, item ->
            // check if item is present
            var isPresent = false
            for (i in 0 until spanCount) {
                if (columnItems[i].contains(item)) {
                    isPresent = true
                    break
                }
            }

            // add item to less full column
            if (!isPresent) {
                var columnIndex = 0
                var min = columnItems[0].size

                // find less full column
                for (i in 0 until spanCount) {
                    if (columnItems[i].size < min) {
                        columnIndex = i
                        min = columnItems[i].size
                    }
                }

                // add new item
                columnItems[columnIndex].add(item)
                val v = onCreateHolder(columns[columnIndex])
                onBindHolder(v, item)
                columns[columnIndex].addView(v)
            }
        }
    }

    fun removeItem(item: T) {
        for (i in 0 until spanCount) {
            val position = columnItems[i].indexOf(item)
            if (position != -1) {
                columnItems[i].removeAt(position)
                columns[i].removeViewAt(position)
                return
            }
        }
    }

    fun updateItem(item: T) {
        for (i in 0 until spanCount) {
            val position = columnItems[i].indexOf(item)
            if (position != -1) {
                columnItems[i].set(position, item)
                onBindHolder(columns[i].getChildAt(position), item)
                return
            }
        }
    }
}