package edu.gwu.tennistalk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*


class MainActivity : AppCompatActivity() {

    // For an explaination of why lateinit var is needed, see:
    // https://docs.google.com/presentation/d/1icewQjn-fkd-wTepzRoqXOjaKWtGUrx0o0Us2anJz3w/edit#slide=id.g615c45607e_0_156
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var signUp: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // onCreate is called the first time the Activity is to be shown to the user, so it a good spot
    // to put initialization logic.
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tells Android which layout file should be used for this screen.
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.logEvent("login_screen_shown", null)

        // Equivalent of a System.out.println (Android has different logging levels to organize logs -- .d is for DEBUG)
        // First parameter = the "tag" allows you to find related logging statements easier (e.g. all logs in the MainActivity)
        // Second parameter = the actual thing you want to log
        Log.d("MainActivity", "onCreate called!")

        val preferences: SharedPreferences = getSharedPreferences("tennis-talk", Context.MODE_PRIVATE)


        // The IDs we are using here should match what was set in the "id" field for our views
        // in our XML layout (which was specified by setContentView).
        // Android will "search" the UI for the elements with the matching IDs to bind to our variables.
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        signUp = findViewById(R.id.signUp)
        progressBar = findViewById(R.id.progressBar)

        // Kotlin shorthand for login.setEnabled(false).
        // If the getter / setter is unambiguous, Kotlin lets you use the property-style syntax
        login.isEnabled = false
        signUp.isEnabled = false

        // Restore the saved username from SharedPreferences and display it to the user when the screen loads.
        // Default to the empty string if there is no saved username.
        val savedUsername = preferences.getString("USERNAME", "")
        username.setText(savedUsername)

        // Using a lambda to implement a View.OnClickListener interface. We can do this because
        // an OnClickListener is an interface that only requires *one* function.
        login.setOnClickListener {
            Toast.makeText(this, "Logging in", Toast.LENGTH_LONG).show()
            // Save the username to SharedPreferences
            val inputtedUsername = username.text.toString()
            val inputtedPassword = password.text.toString()
            firebaseAuth.signInWithEmailAndPassword(inputtedUsername, inputtedPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAnalytics.logEvent("login_successful", null)
                    val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                    Toast.makeText(this, "Logged in as: ${currentUser.email}", Toast.LENGTH_LONG).show()
                    val editor = preferences.edit()
                    editor.putString("USERNAME", inputtedUsername)
                    editor.apply()

                    progressBar.visibility = View.VISIBLE

                    // An Intent is used to start a new Activity.
                    // 1st param == a "Context" which is a reference point into the Android system. All Activities are Contexts by inheritance.
                    // 2nd param == the Class-type of the Activity you want to navigate to.
                    val intent = Intent(this, MenuActivity::class.java)

                    startActivity(intent)
                } else {
                    firebaseAnalytics.logEvent("login_failed", null)

                    val exception = task.exception
                    Toast.makeText(
                        this,
                        "Failed to login: $exception",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        signUp.setOnClickListener {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG).show()
            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()
            firebaseAuth
                .createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                        Toast.makeText(this, "Registered successfully: ${currentUser.email}", Toast.LENGTH_LONG).show()

                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(
                                this,
                                "You didn't supply a valid email address!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else if (exception is FirebaseAuthWeakPasswordException) {
                            Toast.makeText(
                                this,
                                "Your password doesn't meet minimum requirements...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else if (exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                this,
                                "This account already exists!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration error: $exception",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        // Using the same TextWatcher instance for both EditTexts so the same block of code runs on each character.
        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
    }

    // Another example of explicitly implementing an interface (TextWatcher). We cannot use
    // a lambda in this case since there are multiple functions we need to implement.
    //
    // We're defining an "anonymous class" here using the `object` keyword (basically creating
    // a new, dedicated object to implement a TextWatcher for this variable assignment).
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Kotlin shorthand for username.getText().toString()
            // .toString() is needed because getText() returns an Editable (basically a char array).
            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()
            val enableButton: Boolean = inputtedUsername.isNotBlank() && inputtedPassword.isNotBlank()

            // Kotlin shorthand for login.setEnabled(enableButton)
            login.isEnabled = enableButton
            signUp.isEnabled = enableButton
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}
