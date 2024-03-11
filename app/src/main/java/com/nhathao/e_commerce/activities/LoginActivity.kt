package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityLoginBinding
import com.nhathao.e_commerce.models.User
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private var accountName: String? = null
    private var password: String? = null
    private var isRemember: Boolean? = null
    private val requestCodeSignUp = 1
    private val requestCodeHaveAccount = 2
    private val requestCodeForgotPWD = 3
    private val requestCodeChangeAccount = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this.application);

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.revokeAccess()

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
        })

        loadAccount()

        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")

        binding.layoutEdtAccountName.isEndIconVisible = false

        binding.btnBack.setOnClickListener {
            val data = Intent()
            val bundlePassing = Bundle()
            bundlePassing.putBoolean("isLogin", false)
            data.putExtras(bundlePassing)
            setResult(Activity.RESULT_CANCELED, data)
            finish()
        }

        binding.areaForgotPWD.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startForResult.launch(intent)
        }

        binding.btnLogin.setOnClickListener {
            val userAccountName = binding.layoutEdtAccountName.editText?.text.toString()
            val userPWD = binding.layoutEdtPWD.editText?.text.toString()

            val isNotEmpty = checkEmpty(userAccountName, userPWD)

            if (isNotEmpty) {
                login(userAccountName, userPWD)
            }
        }

        binding.btnLoginByGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnLoginByFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        }

        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startForResult.launch(intent)
        }

        binding.layoutEdtAccountName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtAccountName.error = ""
        }

        binding.layoutEdtPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtPWD.error = ""
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userFromFacebook = auth.currentUser
                    if (userFromFacebook != null) {
                        dbRefUser.child(userFromFacebook.uid).get()
                            .addOnCompleteListener { userSnapShot ->
                                if (userSnapShot.isSuccessful) {
                                    if (userSnapShot.result.exists()) {
                                        user = User(
                                            userSnapShot.result.child("userAccountName").value.toString(),
                                            userSnapShot.result.child("userPWD").value.toString(),
                                            userSnapShot.result.child("userName").value.toString(),
                                            userSnapShot.result.child("secretCode").value.toString(),
                                            userSnapShot.result.child("birthday").value.toString(),
                                            userSnapShot.result.child("avatar").value.toString(),
                                            userSnapShot.result.child("email").value.toString(),
                                            userSnapShot.result.child("typeAccount").value.toString()
                                        )
                                    }
                                    else {
                                        user = User(
                                            userFromFacebook.uid,
                                            "",
                                            userFromFacebook.displayName,
                                            "",
                                            "",
                                            "",
                                            "",
                                            "Facebook"
                                        )
                                        val uri = userFromFacebook.photoUrl
                                        val executorService = Executors.newSingleThreadExecutor()
                                        executorService.execute {
                                            try {
                                                val inputStream = java.net.URL(uri.toString()).openStream()
                                                val myBitmap = BitmapFactory.decodeStream(inputStream)
                                                val steam = ByteArrayOutputStream()
                                                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, steam)
                                                val bytes = steam.toByteArray()
                                                user.avatar = Base64.encodeToString(bytes, Base64.DEFAULT)
                                                inputStream?.close()
                                                //Insert User
                                                dbRefUser.child(user.userAccountName!!).setValue(user)
                                            } catch (ex: Exception) {
                                                Toast.makeText(
                                                    this,
                                                    ex.message.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        if (userFromFacebook.email != null)
                                            user.email = userFromFacebook.email
                                    }
                                    Toast.makeText(this, "Sign In Success", Toast.LENGTH_SHORT).show()
                                    val data = Intent()
                                    val bundlePassing = Bundle()
                                    bundlePassing.putInt("requestCode", requestCodeChangeAccount)
                                    bundlePassing.putSerializable("user", user)
                                    bundlePassing.putBoolean("isLogin", true)
                                    data.putExtras(bundlePassing)
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }
                            }
                            .addOnFailureListener { err ->
                                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    else
                        Toast.makeText(this, "Can't login currently. Try after sometime", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login(userAccountName: String, userPWD: String) {
        dbRefUser.child(userAccountName).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = User(
                        it.child("userAccountName").value.toString(),
                        it.child("userPWD").value.toString(),
                        it.child("userName").value.toString(),
                        it.child("secretCode").value.toString(),
                        it.child("birthday").value.toString(),
                        it.child("avatar").value.toString(),
                        it.child("email").value.toString(),
                        it.child("typeAccount").value.toString()
                    )
                    if (user.userPWD == userPWD) {
                        saveAccount()
                        val data = Intent()
                        val bundlePassing = Bundle()
                        bundlePassing.putInt("requestCode", requestCodeChangeAccount)
                        bundlePassing.putSerializable("user", user)
                        bundlePassing.putBoolean("isLogin", true)
                        data.putExtras(bundlePassing)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    } else {
                        binding.layoutEdtPWD.error = "Password doesn't match"
                    }
                } else {
                    binding.layoutEdtAccountName.error = "Account doesn't exist"
                }
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveAccount() {
        isRemember = binding.chkRemember.isChecked
        sharedPreferences = this.getSharedPreferences("saveAccount", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("key_isRemember", isRemember!!)
        if (isRemember!!) {
            accountName = binding.layoutEdtAccountName.editText?.text.toString()
            password = binding.layoutEdtPWD.editText?.text.toString()
            editor.putString("key_accountName", accountName)
            editor.putString("key_password", password)
        }
        editor.apply()
    }

    private fun loadAccount() {
        sharedPreferences = this.getSharedPreferences("saveAccount", Context.MODE_PRIVATE)
        isRemember = sharedPreferences.getBoolean("key_isRemember", false)
        binding.chkRemember.isChecked = isRemember!!
        if (isRemember!!) {
            accountName = sharedPreferences.getString("key_accountName", "")
            password = sharedPreferences.getString("key_password", "")

            binding.layoutEdtAccountName.editText?.setText(accountName)
            binding.layoutEdtPWD.editText?.setText(password)
        }
    }

    private fun checkEmpty(userAccountName: String, userPWD: String): Boolean {
        var isNotEmpty = true

        if (userAccountName.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtAccountName.error = "Field can't be empty"
            binding.layoutEdtAccountName.isEndIconVisible = false
        } else {
            binding.layoutEdtAccountName.error = ""
            binding.layoutEdtAccountName.isEndIconVisible = true
        }
        if (userPWD.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtPWD.error = "Field can't be empty"
        } else {
            binding.layoutEdtPWD.error = ""
        }
        return isNotEmpty
    }

    private fun signInWithGoogle() {
        val signInInTent = googleSignInClient.signInIntent
        activityResultLauncherForGoogle.launch(signInInTent)
    }

    private val activityResultLauncherForGoogle =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userFromGoogle = auth.currentUser
                        if (userFromGoogle != null) {
                            dbRefUser.child(userFromGoogle.uid).get()
                                .addOnCompleteListener { userSnapShot ->
                                    if (userSnapShot.isSuccessful) {
                                        if (userSnapShot.result.exists()) {
                                            user = User(
                                                userSnapShot.result.child("userAccountName").value.toString(),
                                                userSnapShot.result.child("userPWD").value.toString(),
                                                userSnapShot.result.child("userName").value.toString(),
                                                userSnapShot.result.child("secretCode").value.toString(),
                                                userSnapShot.result.child("birthday").value.toString(),
                                                userSnapShot.result.child("avatar").value.toString(),
                                                userSnapShot.result.child("email").value.toString(),
                                                userSnapShot.result.child("typeAccount").value.toString()
                                            )
                                        }
                                        else {
                                            user = User(
                                                userFromGoogle.uid,
                                                "",
                                                userFromGoogle.displayName,
                                                "",
                                                "",
                                                "",
                                                userFromGoogle.email,
                                                "Google"
                                            )
                                            val uri = userFromGoogle.photoUrl
                                            val executorService = Executors.newSingleThreadExecutor()
                                            executorService.execute {
                                                try {
                                                    val inputStream = java.net.URL(uri.toString()).openStream()
                                                    val myBitmap = BitmapFactory.decodeStream(inputStream)
                                                    val steam = ByteArrayOutputStream()
                                                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, steam)
                                                    val bytes = steam.toByteArray()
                                                    user.avatar = Base64.encodeToString(bytes, Base64.DEFAULT)
                                                    inputStream?.close()
                                                    //Insert User
                                                    dbRefUser.child(user.userAccountName!!).setValue(user)
                                                } catch (ex: Exception) {
                                                    Toast.makeText(
                                                        this,
                                                        ex.message.toString(),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                        Toast.makeText(this, "Sign In Success", Toast.LENGTH_SHORT).show()
                                        val data = Intent()
                                        val bundlePassing = Bundle()
                                        bundlePassing.putInt("requestCode", requestCodeChangeAccount)
                                        bundlePassing.putSerializable("user", user)
                                        bundlePassing.putBoolean("isLogin", true)
                                        data.putExtras(bundlePassing)
                                        setResult(Activity.RESULT_OK, data)
                                        finish()
                                    }
                                }
                                .addOnFailureListener { err ->
                                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                    } else
                        Toast.makeText(this, "Can't login currently. Try after sometime", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Sign In Failed, Try again later", Toast.LENGTH_SHORT).show()
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    val requestCode = bundleGet.getInt("requestCode", 0)
                    if (requestCode == requestCodeHaveAccount) {
                        binding.layoutEdtAccountName.editText?.text?.clear()
                        binding.layoutEdtPWD.editText?.text?.clear()
                    } else if (requestCode == requestCodeSignUp || requestCode == requestCodeForgotPWD) {
                        val user = bundleGet.getSerializable("user") as User
                        binding.layoutEdtAccountName.editText?.setText(user.userAccountName)
                        binding.layoutEdtPWD.editText?.setText(user.userPWD)
                    }
                }
            }
        }

}