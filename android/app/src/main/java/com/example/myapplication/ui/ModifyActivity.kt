package com.example.myapplication.ui

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.myapplication.api.ApiService
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.UserModify
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityModifyBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_modify.*
import kotlinx.android.synthetic.main.activity_modify.editName
import kotlinx.android.synthetic.main.activity_room_modify.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*

class ModifyActivity: AppCompatActivity() {

    val TAG = "ModifyActivity"

    var apiService: ApiService? = null
    var picUri: Uri? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected: ArrayList<String> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()
    private val ALL_PERMISSIONS_RESULT = 107
    private val IMAGE_RESULT = 200
    var mBitmap: Bitmap? = null

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivityModifyBinding

    var user_id: Int = 0
    var name = ObservableField<String>()
    var PW = ObservableField<String>()
    var email = ObservableField<String>()
    var profile = "http://d3c30c5e052c.ngrok.io/uploads/798545ff1676d64dd4faea6b36bb0f94.png"
    var field = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify)
        binding.activity = this@ModifyActivity

        // 퍼미션 요청
        askPermissions()
        // 레트로핏 설정
        initRetrofitClient()

        getImage()

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        editName.setText(preferences.getString("userName", ""))

        name.set(preferences.getString("userName", "없음"))

        if (intent.hasExtra("field")) {
            field = intent.getStringExtra("field").toString()
            val arrayList = field.split(",")
            for (value in arrayList) {
                when (value) {
                    "스터디" -> binding.cbStudy.isChecked = true
                    "게임" -> binding.cbGame.isChecked = true
                    "업무" -> binding.cbWork.isChecked = true
                    "음악" -> binding.cbMusic.isChecked = true
                    "미술" -> binding.cbArt.isChecked = true
                    "운동(헬스)" -> binding.cbExercise.isChecked = true
                    "기타" -> binding.cbEtc.isChecked = true
                }
            }
        }

        binding.imgProfile.setOnClickListener {
            startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT)
        }
    }

    fun onSave(view: View) {
        profile = preferences.getString("userProfile", "").toString()
        user_id = preferences.getString("userNum", "0")!!.toInt()
        field = ""

        if (name.get() == "") {
            showToast("이름을 입력해주세요")
        } else if (email.get() == "") {
            showToast("이메일을 입력해주세요")
        } else if (PW.get() == "") {
            showToast("현재 비밀번호를 입력해주세요")
        } else if (email.get() != "" && PW.get() != "") {
            if (preferences.getString("userId", "") != email.get()) {
                showToast("일치하지 않는 아이디 입니다.")
            }
            if (preferences.getString("userPW", "") != PW.get()) {
                showToast("일지하지 않는 비밀번호 입니다.")
            }

            if (preferences.getString("userId", "") == email.get() && preferences.getString("userPW", "") == PW.get()) {

                field = ""
                if (binding.cbStudy.isChecked) field += "${binding.cbStudy.text},"
                if (binding.cbWork.isChecked) field += "${binding.cbWork.text},"
                if (binding.cbGame.isChecked) field += "${binding.cbGame.text},"
                if (binding.cbMusic.isChecked) field += "${binding.cbMusic.text},"
                if (binding.cbArt.isChecked) field += "${binding.cbArt.text},"
                if (binding.cbExercise.isChecked) field += "${binding.cbExercise.text},"
                if (binding.cbEtc.isChecked) field += "${binding.cbEtc.text},"

                if(field.length >0) {
                    field = field.substring(0, field.length - 1)
                }

                val user = getData()
                val call = RetrofitHelper.getApiService().modify(user)
                call.enqueue(object : Callback<UserModify>{
                    override fun onResponse(call: Call<UserModify>, response: Response<UserModify>) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                val editor = preferences.edit()
                                editor.putString("userName", name.get())
                                editor.putString("userProfile", profile)
                                showToast("수정되었습니다.")
                                finish()
                            }
                        } else Log.e(TAG, response.message())
                    }
                    override fun onFailure(call: Call<UserModify>, t: Throwable) {
                        Log.e(TAG, "통신안됨: ${t.message}")
                    }

                })
            }
        }
    }

    fun getData(): UserModify {
        val data = UserModify(user_id, name.get().toString(), profile, field)
        return data
    }

    fun getImage() {
        Picasso.get().load("http://d3c30c5e052c.ngrok.io/uploads/798545ff1676d64dd4faea6b36bb0f94.png").into(binding.imgProfile)
    }

    // region 프로필 사진 서버에 저장하기
    // 퍼미션 요청
    private fun askPermissions() {
        // 카메라
        //permissions.add(CAMERA)
        // 외부 저장소 쓰기/읽기
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        // 요청하지 않은 퍼미션 찾아내기
        permissionsToRequest = findUnAskedPermissions(permissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0) requestPermissions(
                    permissionsToRequest!!.toArray(
                            arrayOfNulls<String>(permissionsToRequest!!.size)
                    ), ALL_PERMISSIONS_RESULT
                    // 요청하기
            )
        }
    }

    // 레드로핏 설정
    private fun initRetrofitClient() {
        val client = OkHttpClient.Builder().build()
        apiService =
                Retrofit.Builder().baseUrl("http://d3c30c5e052c.ngrok.io").client(client).build().create(
                        ApiService::class.java
                )
    }

    // 이미지 선택하기 (가져오기)
    fun getPickImageChooserIntent(): Intent? {
        val outputFileUri: Uri = getCaptureImageOutputUri()!!
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = packageManager
        // 이미지 선택 Intent
        val captureIntent = Intent(Intent.ACTION_GET_CONTENT)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }

    // 이미지 Uri 가져오기
    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = getExternalFilesDir("")
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "profile.png"))
        }
        return outputFileUri
    }

    // startActivityForResult 끝나고
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_RESULT) {
                // 파일 경로 가져오기
                val filePath = getImageFilePath(data)
                if (filePath != null) {
                    // 파일 비트맵으로 만들기
                    mBitmap = BitmapFactory.decodeFile(filePath)
                    // 이미지 뷰에 보이기
                    binding.imgProfile.setImageBitmap(mBitmap)
                }
            }
        }
        if (mBitmap != null) {
            multipartImageUpload()
        } else {
            Toast.makeText(applicationContext, "비트맵 비어있음", Toast.LENGTH_SHORT).show()
        }
    }

    // 파일경로를 사용하여 이미지 가져오기
    private fun getImageFromFilePath(data: Intent?): String? {
        val isCamera = data == null || data.data == null
        return if (isCamera) getCaptureImageOutputUri()!!.path else getPathFromURI(data!!.data)
    }

    // 이미지 경로 가져오기
    fun getImageFilePath(data: Intent?): String? {
        return getImageFromFilePath(data)
    }

    // URI를 이용하여 경로 가져오기
    private fun getPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri!!, proj, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("pic_uri", picUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri")
    }

    // 요청하지 않은 퍼미션 찾아내기
    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String>? {
        val result = ArrayList<String>()
        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    // 퍼미션이 있는지
    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    @TargetApi(Build.VERSION_CODES.M)

    // 드디어 올린다 서버에
    private fun multipartImageUpload() {
        try {
            val filesDir: File = applicationContext.filesDir
            val file = File(filesDir, "image" + ".png")
            val bos = ByteArrayOutputStream()
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata: ByteArray = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
            val name = RequestBody.create(MediaType.parse("text/plain"), "upload")
            val req: Call<ResponseBody> = apiService!!.postImage(body, name)
            req.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Log.e("sendImageProfile", "성공입니당")
                    }

                    Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_SHORT).show();
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("sendImageProfile", "실패입니당")
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }

            })
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms!!)) {
                        permissionsRejected.add(perms!!)
                    }
                }
                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel(
                                    "These permissions are mandatory for the application. Please allow access."
                            ) { dialog, which ->
                                requestPermissions(
                                        permissionsRejected.toTypedArray(),
                                        ALL_PERMISSIONS_RESULT
                                )
                            }
                            return
                        }
                    }
                }
            }
        }
    }
    // endregion

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}