package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator

class EditActivity : AppCompatActivity() {

    lateinit var backgroundLayout: ConstraintLayout
    lateinit var windowLayout: ConstraintLayout
    private val dbHelper = DBHelper(this)

    companion object {
        const val RESULT_KEY = "RESULT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        backgroundLayout = findViewById(R.id.background)
        windowLayout = findViewById(R.id.window)

        val id = intent.getLongExtra(MainActivity.ITEM_ID_KEY, 0)
        val user = dbHelper.getById(id)
        val editName = findViewById<EditText>(R.id.editName)
        val editNumber = findViewById<EditText>(R.id.editNumber)
        val editSurname = findViewById<EditText>(R.id.editSurname)
        val editDob = findViewById<EditText>(R.id.editDob)

        editName.setText(user?.name)
        editNumber.setText(user?.number)
        editSurname.setText(user?.surname)
        editDob.setText(user?.dob)


        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            val newName = editName.text.toString()
            val newNumber = editNumber.text.toString()
            val newSurname = editSurname.text.toString()
            val newDob = editDob.text.toString()
            dbHelper.updateTask(id, newName, newSurname, newNumber, newDob)
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_KEY,newName)
            //returnIntent.putExtra(RESULT_KEY,newNumber)
            //returnIntent.putExtra(RESULT_KEY,newSurname)
            //returnIntent.putExtra(RESULT_KEY,newDob)

            returnIntent.putExtra(MainActivity.ITEM_ID_KEY, id)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            //val newTitle = editName.text.toString()
            //dbHelper.deleteTask(id)
            dbHelper.getById(id)?.id?.let { it1 -> dbHelper.deleteTask(it1) }
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_KEY, id)
           // returnIntent.putExtra(MainActivity.ITEM_ID_KEY, id)
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }

        setActivityStyle()
    }



    @SuppressLint("RestrictedApi")
    private fun setActivityStyle() {

        // Make the background full screen, over status bar
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        this.window.statusBarColor = Color.TRANSPARENT
        val winParams = this.window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        this.window.attributes = winParams

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            backgroundLayout.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        windowLayout.alpha = 0f
        windowLayout.animate().alpha(1f).setDuration(500)
            .setInterpolator(DecelerateInterpolator()).start()

        // Close window when you tap on the dim background
        backgroundLayout.setOnClickListener { onBackPressed() }
        windowLayout.setOnClickListener { /* Prevent activity from closing when you tap on the popup's window background */ }
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            backgroundLayout.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        windowLayout.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

}