package com.hud.systemwindow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

data class Task(var title: String, var done: Boolean)

class TasksActivity : AppCompatActivity() {

    private val tasks = mutableListOf<Task>()
    private lateinit var listView: ListView
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        listView = findViewById(R.id.listTasks)
        loadTasks()
        adapter = TaskAdapter()
        listView.adapter = adapter

        val etTask = findViewById<EditText>(R.id.etTask)
        findViewById<android.widget.Button>(R.id.btnAddTask).setOnClickListener {
            val text = etTask.text.toString().trim()
            if (text.isNotEmpty()) {
                tasks.add(Task(text, false))
                saveTasks()
                etTask.text.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadTasks() {
        tasks.clear()
        val arr = JSONArray(Prefs.getTasksJson(this))
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            tasks.add(Task(obj.getString("title"), obj.getBoolean("done")))
        }
    }

    private fun saveTasks() {
        val arr = JSONArray()
        tasks.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("done", it.done)
            arr.put(obj)
        }
        Prefs.setTasksJson(this, arr.toString())
    }

    private inner class TaskAdapter : ArrayAdapter<Task>(this@TasksActivity, 0, tasks) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
            val task = tasks[position]
            val title = view.findViewById<TextView>(R.id.tvTaskTitle)
            val checkbox = view.findViewById<CheckBox>(R.id.cbDone)
            val delete = view.findViewById<ImageButton>(R.id.btnDelete)

            title.text = task.title
            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = task.done
            checkbox.setOnCheckedChangeListener { _, checked ->
                task.done = checked
                saveTasks()
            }
            delete.setOnClickListener {
                tasks.removeAt(position)
                saveTasks()
                notifyDataSetChanged()
            }
            return view
        }
    }
}
