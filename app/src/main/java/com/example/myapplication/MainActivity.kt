package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val list = mutableListOf<Task>()
    private val adapter = RecyclerAdapter(list)
    private val dbHelper = DBHelper(this)
    var srch = ""
    companion object {
        const val REQUEST_CODE = 1
        const val ITEM_ID_KEY = "ITEM_ID_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.addAll(dbHelper.getAllTasks())

        adapter.onItemClick = {
            // open EditActivity
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(ITEM_ID_KEY, list[it].id)
            startActivityForResult(intent, REQUEST_CODE)
        }

        ////////
        val search = findViewById<EditText>(R.id.search)
        search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                srch = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        search.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                filtration(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val editName = findViewById<EditText>(R.id.editTextTextPersonName)
        val editNumber = findViewById<EditText>(R.id.editTextTextPersonNumber)
        val editSurname = findViewById<EditText>(R.id.editTextTextPersonSurname)
        val editDob = findViewById<EditText>(R.id.editTextTextPersonDob)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val name = editName.text.toString()
            val number = editNumber.text.toString()
            val surname = editSurname.text.toString()
            val dob = editDob.text.toString()

            if (name.isNotBlank() || number.isNotBlank() || surname.isNotBlank() || dob.isNotBlank()) {
                editName.text.clear()
                editNumber.text.clear()
                editSurname.text.clear()
                editDob.text.clear()

                val id = dbHelper.addTask(name, surname,number,dob)   //VAJNO!!!!
                list.add(Task(id, name, surname,number,dob))
                adapter.notifyItemInserted(list.lastIndex)
            }

        }
    }

    fun filtration(nanba:String){
        if (nanba == ""){
            adapter.updateList(list)
        }else {
            val filtration_list = list.filter { it.name.contains(nanba, true) ||
                                                it.surname.contains(nanba, true)
            }
            adapter.updateList(filtration_list)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            // получение данных от Activity2
            val id = data?.getLongExtra(EditActivity.RESULT_KEY ,0)
            val index = list.indexOfFirst { it.id == id }
            //val index2 = filtration_list.indexOfFirst { it.id == id }
            list.removeAt(index)
            //filtration_list.removeAt(index2)
            adapter.notifyItemRemoved(index)
            // в result лежит строка "тут какой-то результат (строка)"
        }

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(EditActivity.RESULT_KEY)
            val id = data?.getLongExtra(ITEM_ID_KEY, 0)

            if (id != null && result != null) {
                // change list item
                val index = list.indexOfFirst { it.id == id }
                list[index].name = result
                // redraw list
                adapter.notifyItemChanged(index)
            }
        }
    }
}