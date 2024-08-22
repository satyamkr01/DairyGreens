package com.unifydream.dairygreens.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.unifydream.dairygreens.R
import com.unifydream.dairygreens.databinding.ActivityOtpAuthBinding

class OTPAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otp_auth_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        binding.verifyBtn.setOnClickListener {
            val otp = binding.idOtp.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        /*firebaseAuth = FirebaseAuth.getInstance()

        otp = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        binding.otpProgressBar.visibility = View.INVISIBLE
        addTextChangeListener()
        resendOTPTvVisibility()

        binding.resendTextView.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }

        binding.verifyOTPBtn.setOnClickListener {
            val typedOTP = (binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() +
                    binding.otpEditText3.text.toString() + binding.otpEditText4.text.toString() +
                    binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString())

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typedOTP)
                    binding.otpProgressBar.visibility = View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Please enter correct OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(applicationContext, BaseActivity::class.java))
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

/*    private fun resendOTPTvVisibility() {
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.resendTextView.visibility = View.INVISIBLE
        binding.resendTextView.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            binding.resendTextView.visibility = View.VISIBLE
            binding.resendTextView.isEnabled = true
        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("TAG", "onVerificationFailed: $e")
            }
            binding.otpProgressBar.visibility = View.VISIBLE
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            otp = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "OTP authenticated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BaseActivity::class.java))
                    finish()
                } else {
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this , "Entered invalid OTP!", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.otpProgressBar.visibility = View.VISIBLE
            }
    }

    private fun addTextChangeListener() {
        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()
            }
        }
    }*/
}