package com.app.ui.activity
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.content.Intent
import com.app.ui.activity.LoginActivity
import android.content.SharedPreferences
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.app.R

//import android.support.v7.app.AppCompatActivity;
//import itcast.cn.boxuegu.R;
//import itcast.cn.boxuegu.utils.AnalysisUtils;
//import itcast.cn.boxuegu.utils.MD5Utils;
class ModifyPswActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_psw)
       val prefs = getPreferences(Context.MODE_PRIVATE)
       val editor = prefs.edit()
//        val accountEdit = findViewById<EditText>(R.id.accountEdit)
//        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)
//        val rememberPass = findViewById<CheckBox>(R.id.rememberPass)
//        val isRemember = prefs.getBoolean("remember_password",false)
//        if (isRemember){
//            val account = prefs.getString("account","")
//            val password = prefs.getString("password","")
//            accountEdit.setText(account)
//            passwordEdit.setText(password)
//            rememberPass.isChecked=true
//        }
        val accountEdit = findViewById<EditText>(R.id.edt_UserName)
        val passwordEdit = findViewById<EditText>(R.id.edt_NewPassWord)
        val btn_Confirm = findViewById<Button>(R.id.btn_Confirm)

        btn_Confirm.setOnClickListener {
            val account = accountEdit.text.toString()
            val password = passwordEdit.text.toString()
//            if (account == "admin" && password == "123456") {
//                if (rememberPass.isChecked) {
//                    //存储数据
//                    editor.putBoolean("remember_password", true)
                    editor.putString("account", account)
                    editor.putString("password", password)
//                }else{
//                    editor.clear()
//                }
                editor.apply()
                val intent = Intent(this@ModifyPswActivity, MainActivity::class.java)
                startActivity(intent)
//            } else {
//                Toast.makeText(this@ModifyPswActivity, "account or password is invalid", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}