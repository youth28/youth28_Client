package com.example.myapplication.view

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
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.ActivityModifyProfileBinding
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.room.ResponseLogin
import com.example.myapplication.dto.user.UserDTO
import com.example.myapplication.dto.id.UserId
import kotlinx.android.synthetic.main.activity_modify_profile.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ModifyProfileActivity : AppCompatActivity() {

    val TAG = "ModifyProfile"
    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityModifyProfileBinding

    var picUri: Uri? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected: ArrayList<String> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()
    private val ALL_PERMISSIONS_RESULT = 107
    private val IMAGE_RESULT = 200
    var mBitmap: Bitmap? = null

    private lateinit var loadDialog: SweetAlertDialog

    var imgBool = false
    var mode = ""
    val btnName = MutableLiveData<String>()
    val mainMsg = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_profile)
        binding.activity = this

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        btnName.observe(this, {
            binding.button.text = "${it}"
            imgBool = !(it == "프로필 사진은 바꾸지 않겠습니다." || it == "다음에 바꾸겠습니다.")
        })

        mainMsg.observe(this, {
            binding.tvMainMsg.text = "${it}"
        })

        // 퍼미션 요청
        askPermissions()

        if (intent.hasExtra("mode")) {
            mode = intent.getStringExtra("mode").toString()
            Log.e("intentMode", mode)
            when(mode) {
                "1","2","4"  -> {
                    btnName.value = "다음에 바꾸겠습니다."
                    mainMsg.value = "프로필 사진을 변경하려면 아래 사진을 선택해주세요"
                }
                "3" -> {
                    btnName.value = "프로필 사진은 바꾸지 않겠습니다."
                    mainMsg.value = "프로필 사진을 변경하려면 아래 사진을 선택해주세요"
                }

            }
        }

       binding.imgProfile.setOnClickListener {
            startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT)
        }
    }

    fun onLogin() {
        val email: String = UserData.userId
        val PW: String = UserData.userPassword
        Log.e("login", "email=$email, password=$PW")

        // 로그인 통신 코드
        val user = UserDTO(email, PW)
        val call = RetrofitHelper.getUserApi().login(user)
        call.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            val intent = Intent(this@ModifyProfileActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else Log.e("$TAG Err", "통신안됨: ${response.message()}")
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.e("$TAG Err", "통신안됨: ${t.message}")
            }

        })
    }

    fun onBtn() {
        if (imgBool) {
            if (mBitmap != null) {
                multipartImageUpload()
            } else {
                showToast("비트맵 비어있음")
            }
        } else {
            // 이미지 안바꾸겠다.
            when(mode) {
                "1" -> onLogin()
                "2" -> finish()
                "3" -> finish()
                "4" -> finish()
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    // 드디어 올린다 서버에
    private fun multipartImageUpload() {
        try {
            val filesDir: File = applicationContext.filesDir
            val file = File(filesDir, "image" + ".png")
            val bos = ByteArrayOutputStream()
            loadDialog = SweetAlertDialog(this@ModifyProfileActivity, SweetAlertDialog.PROGRESS_TYPE)
            loadDialog.progressHelper.barColor = Color.parseColor("#36b8ff")
            loadDialog.titleText = "이미지 프로필을 변경중입니다..."
            loadDialog.setCancelable(false)
            loadDialog.show()
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata: ByteArray = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
            val name = RequestBody.create(MediaType.parse("text/plain"), "upload")
            val req: Call<ResponseBody>
            val userId: Int = UserData.userNum.toInt()
            req = when(mode) {
                "1" -> RetrofitHelper.getImageApi().postImage(UserId(userId), body, name)
                "2" -> RetrofitHelper.getImageApi().modifyImage(UserId(userId), body, name)
                "3" -> RetrofitHelper.getImageApi().modifyRoomImage(RoomId(RoomData.roomId), body, name)
                "4" -> RetrofitHelper.getImageApi().postRoomImage(RoomId(RoomData.roomId), body, name)
                else -> RetrofitHelper.getImageApi().modifyImage(UserId(userId), body, name)
            }

            req.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Log.e("sendImageProfile", "성공입니당")
                        loadDialog.dismiss()
                        when (mode) {
                            "1" -> onLogin()
                            "2" -> finish()
                            "3" -> finish()
                            "4" -> finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("sendImageProfile", "실패입니당 ${t.message}")
                    t.printStackTrace();
                    showToast("서버 오류로 인해 이미지 요청은 나중에 하주세요")
                    loadDialog.dismiss()
                    when (mode) {
                        "1" -> onLogin()
                        "2" -> finish()
                        "3" -> finish()
                        "4" -> finish()
                    }
                }

            })
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // region 프로필 사진 서버에 저장하기
    // 퍼미션 요청
    private fun askPermissions() {
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
                    btnName.value = "이걸로 할게요!!"
                }
            }
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