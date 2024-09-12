package com.test.bytetask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.test.bytetask.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private var binding : ActivitySignInBinding? = null
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding?.buttonSignIn?.setOnClickListener {
            val email = binding?.emailEt?.text?.toString()!!
            val pass = binding?.passET?.text?.toString()!!

            if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

            }else{
                Toast.makeText(this,"Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            }

        }

        binding?.tvSignUp?.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}