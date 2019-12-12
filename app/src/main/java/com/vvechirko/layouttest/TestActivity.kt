package com.vvechirko.layouttest

import android.graphics.Color
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

        val adapter = object : ColumnsListView.Adapter<Order>() {
            override fun onCreateHolder(parent: ViewGroup): View {
                return LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_order, parent, false).also {
                        it.layoutParams.height = 3 * Random.nextInt(100, 200)
                    }
            }

            override fun onBindHolder(view: View, item: Order) {
                (view as TextView).text = item.id
                view.setOnClickListener {
                    if (item.status == "done") {
                        removeItem(item)
                    } else {
                        it.setBackgroundColor(Color.CYAN)
                        item.status = "done"
                        updateItem(item)
                    }
                }
            }
        }
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