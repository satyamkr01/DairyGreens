package com.unifydream.dairygreens.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.hbb20.CountryCodePicker
import com.unifydream.dairygreens.R
import com.unifydream.dairygreens.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val PHONE_NUMBER_LENGTH = 10
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var ccp : CountryCodePicker
    private lateinit var phoneNumberEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        ccp = findViewById(R.id.ccp)
        phoneNumberEditText = findViewById(R.id.phoneNumber)
        ccp.registerCarrierNumberEditText(phoneNumberEditText)

        if (auth.currentUser != null) {
            startActivity(Intent(applicationContext, BaseActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            validateAndSendCode()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("TAG", "onVerificationFailed: ${e.message.toString()}")
                Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val intent = Intent(applicationContext, OTPAuthActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("resendToken", resendToken)
                startActivity(intent)
            }
        }
    }

    private fun validateAndSendCode() {
        val phoneNumber = phoneNumberEditText.text.toString().replace(" ","")

        if (phoneNumber.isNotEmpty() && phoneNumber.length == PHONE_NUMBER_LENGTH) {
            val fullNumber = ccp.fullNumberWithPlus.replace(" ","")
            sendVerificationCode(fullNumber)
        } else if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter your mobile number", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP authenticated successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, BaseActivity::class.java))
                    finish()
                } else {
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Entered invalid OTP!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

/*        firebaseAuth = FirebaseAuth.getInstance()
        binding.phoneProgressBar.visibility = View.INVISIBLE

        binding.sendOTPBtn.setOnClickListener {
            phoneNumber = binding.phoneEditTextNumber.text.trim().toString()
            if (phoneNumber.isNotEmpty()) {
                if (phoneNumber.length == 10) {
                    phoneNumber = "+91$phoneNumber"
                    binding.phoneProgressBar.visibility = View.VISIBLE
                    val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(this, "Please enter correct number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
        }*/
    //}

/*    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, BaseActivity::class.java))
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP authenticated successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BaseActivity::class.java))
                    finish()
                } else {
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Entered invalid OTP!", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.phoneProgressBar.visibility = View.INVISIBLE
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            }
            binding.phoneProgressBar.visibility = View.VISIBLE
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            val intent = Intent(this@LoginActivity, OTPAuthActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }*/
}