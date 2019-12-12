package com.vvechirko.layouttest

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val list = mutableListOf<Order>()
        for (i in 0 until 20) {
            list.add(Order((i + 1).toString()))
        }

        val adapter = ColumnsAdapter()
        listView.setAdapter(adapter)

        adapter.addItems(list.subList(0, 10))

        Handler().postDelayed({
            adapter.addItems(list.subList(8, 16))
        }, 10000)

        Handler().postDelayed({
            adapter.addItems(list.subList(14, 20))
        }, 20000)
    }
}

class ColumnsAdapter : ColumnsListView.Adapter<Order>() {
    override fun onCreateHolder(parent: ViewGroup): ColumnsListView.Holder<Order> {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        )
    }

    override fun onBindHolder(holder: ColumnsListView.Holder<Order>, item: Order) {
        holder.bind(item)
        holder.view.setOnClickListener {
            if (item.status == "done") {
                removeItem(item)
            } else {
                item.status = "done"
                updateItem(item)
            }
        }
    }

    class Holder(view: View) : ColumnsListView.Holder<Order>(view) {
        init {
            // simulate different heights
            view.layoutParams.height = 3 * Random.nextInt(100, 200)
        }

        override fun bind(item: Order) {
            (view as TextView).text = item.id
            view.isActivated = item.status == "done"
        }
    }
}