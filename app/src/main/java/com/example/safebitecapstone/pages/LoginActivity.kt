package com.example.safebitecapstone.pages

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import com.example.safebitecapstone.R
import com.example.safebitecapstone.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private var emailKosongFlag = false
    private var passwordKosongFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.signInButton.setOnClickListener {
            signIn()
        }
//        emailFieldChecker()
//        passwordFieldChecker()

//        binding.buttonLogin.isEnabled = false
//        binding.buttonLogin.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java )
//            startActivity(intent)
//            finish()
//        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

//    private fun setMyButtonEnable(){
//        binding.buttonLogin.isEnabled = emailKosongFlag && passwordKosongFlag && binding.emailField.error == null
//    }
//
//    private fun emailFieldChecker(){
//        binding.emailField.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(email: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(email?.isEmpty() == true){
//                    binding.emailFieldLayout.error = null
//                }
//                else{
//                    if (!Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
//                        binding.emailFieldLayout.error = "Email Invalid"
//                    }
//                    else{
//                        binding.emailFieldLayout.error = null
//                    }
//                }
//
//                emailKosongFlag = email.toString().trim { it <= ' ' }.isNotEmpty()
//                setMyButtonEnable()
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//        })
//    }
//
//    private fun passwordFieldChecker(){
//        binding.passwordField.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(password: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                passwordKosongFlag = password.toString().trim { it <= ' ' }.isNotEmpty()
//                setMyButtonEnable()
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//        })
//    }
}