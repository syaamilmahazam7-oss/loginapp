package com.example.loginapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.File

/**
 * Kongsi akaun yang sedang login dengan app lain (cth Belalang) melalui fail kecil:
 *   /sdcard/BelalangSuite/account.json  ->  {"email": "...", "uid": "...", "ts": ...}
 *
 * Cuma email + uid disimpan. ROLE sengaja TIDAK disimpan di sini supaya app lain
 * sentiasa semak role terkini terus dari Firestore (admin tukar role -> terus berkesan).
 */
object AccountShare {

    private const val DIR_NAME = "BelalangSuite"
    private const val FILE_NAME = "account.json"
    private const val REQ_WRITE_STORAGE = 4021

    private fun shareFile(): File =
        File(File(Environment.getExternalStorageDirectory(), DIR_NAME), FILE_NAME)

    /** Android 11+: "All files access". Android 10 ke bawah: WRITE_EXTERNAL_STORAGE. */
    fun hasAccess(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    /** Tulis akaun semasa. Senyap kalau belum login / belum ada akses storan. */
    fun write(context: Context): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val email = user.email ?: return false
        if (!hasAccess(context)) return false
        return try {
            val file = shareFile()
            file.parentFile?.mkdirs()
            val json = JSONObject()
                .put("email", email)
                .put("uid", user.uid)
                .put("ts", System.currentTimeMillis())
            file.writeText(json.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Padam fail bila logout supaya app lain tak guna akaun lama. */
    fun clear() {
        try {
            shareFile().delete()
        } catch (e: Exception) {
            // abaikan
        }
    }

    /** Dipanggil selepas user dah masuk Dashboard (bukan masa app baru dibuka). */
    fun promptForAccess(activity: Activity) {
        if (hasAccess(activity)) return
        AlertDialog.Builder(activity)
            .setTitle("Benarkan akses storan")
            .setMessage(
                "Supaya app anda yang lain (cth Belalang) boleh kenal akaun yang sedang login di sini, " +
                    "LoginApp perlu simpan satu fail kecil (email sahaja, bukan kata laluan).\n\n" +
                    "Tekan Benarkan, kemudian hidupkan \"Allow access to manage all files\" untuk LoginApp."
            )
            .setPositiveButton("Benarkan") { _, _ -> openAccessSettings(activity) }
            .setNegativeButton("Nanti", null)
            .show()
    }

    private fun openAccessSettings(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                activity.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + activity.packageName)
                    )
                )
            } catch (e: Exception) {
                activity.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQ_WRITE_STORAGE
            )
        }
    }
}
