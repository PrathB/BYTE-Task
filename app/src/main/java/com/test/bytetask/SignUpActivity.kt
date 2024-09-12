package com.test.bytetask

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.test.bytetask.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    private  var binding : ActivitySignUpBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding?.buttonSignUp?.setOnClickListener {
            val email = binding?.emailEt?.text?.toString()!!
            val pass = binding?.passET?.text?.toString()!!
            val confirmPass = binding?.confirmPassEt?.text?.toString()!!

            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if(pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent = Intent(this,SignInActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Empty fields are not allowed!!",Toast.LENGTH_SHORT).show()
            }

        }

        binding?.tvSignIn?.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
    }
}