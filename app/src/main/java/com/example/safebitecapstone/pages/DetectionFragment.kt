package com.example.safebitecapstone.pages

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.safebitecapstone.R
import com.example.safebitecapstone.databinding.FragmentDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class DetectionFragment : Fragment() {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val GALLERY_REQUEST_CODE = 1234
    private val WRITE_EXTERNAL_STORAGE_CODE = 1

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    lateinit var finalUri: Uri
    var bitmap: Bitmap? = null
    var scannedText: String? = null

    private lateinit var binding: FragmentDetectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        checkPermission()
        requestPermissionPermit()
        binding.buttonProcess.isEnabled = false

        binding.buttonGallery.setOnClickListener {
            if(checkPermission()){
                pickFromGallery()
            }
            else{
                Toast.makeText(activity, "Allow all permissions before continue", Toast.LENGTH_SHORT).show()
//                requestPermission()
            }
        }

        binding.buttonPhoto.setOnClickListener {

        }

        binding.buttonDetect.setOnClickListener {
            saveEditedImage()
            processImage()

            binding.progressBar.visibility = View.VISIBLE
            binding.buttonDetect.visibility = View.GONE
            binding.resultScan.visibility = View.GONE
            binding.buttonProcess.visibility = View.VISIBLE
            binding.loadingInformation.visibility = View.VISIBLE
        }

        binding.buttonProcess.setOnClickListener {
            val intent = Intent(activity, DetailScanActivity::class.java)
            val ingridient = binding.resultScan.text
            intent.putExtra(DetailScanActivity.EXTRA_INGRIDIENT, ingridient.toString())
            intent.putExtra(DetailScanActivity.EXTRA_IMG, finalUri)
            startActivity(intent)
        }

        binding.buttonCancel.setOnClickListener {
            binding.buttonDetect.visibility = View.GONE
            binding.buttonCancel.visibility = View.GONE

            binding.buttonPhoto.visibility = View.VISIBLE
            binding.buttonGallery.visibility = View.VISIBLE
            binding.buttonProcess.visibility = View.GONE
            binding.resultScan.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.photoPlaceholder.setImageResource(R.drawable.img_placeholder)
        }

        binding.resultScan.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(result: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(result: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (result?.isEmpty() != true){
                    binding.buttonProcess.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.buttonProcess.visibility = View.VISIBLE
                    binding.loadingInformation.visibility = View.GONE
                }
                else{
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(result: Editable?) {
            }

        })

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {

                    var extras: Bundle? = result.data?.extras

                    var imageUri: Uri

                    var imageBitmap = extras?.get("data") as Bitmap

                    var imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )

                    var bm = imageResult.get()

                    imageUri = context?.let { saveImage(bm, it) }!!
                    launchImageCrop(imageUri)
                }
            }
    }


    private fun setImage(uri: Uri?){
        Glide.with(this)
            .load(uri)
            .into(binding.photoPlaceholder)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            WRITE_EXTERNAL_STORAGE_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(activity, "Enable permissions", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun saveEditedImage() {


        bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, finalUri)
//        processImage()
//        saveMediaToStorage(bitmap!!)
    }


    private fun saveImage(image: Bitmap?, context: Context): Uri {
        var imageFolder= File(context.cacheDir,"images")
        var uri: Uri? = null

        try {
            imageFolder.mkdirs()
            var file: File = File(imageFolder,"captured_image.png")
            var stream: FileOutputStream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri= FileProvider.getUriForFile(context.applicationContext,"com.sunayanpradhan.imagecropper"+".provider",file)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return uri!!
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun pickFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }


    private fun processImage(){
        if (bitmap!=null) {
            val image = bitmap?.let {
                InputImage.fromBitmap(it, 0)
            }

            image?.let {
                recognizer.process(it)
                    .addOnSuccessListener { visionText ->
                        binding.resultScan.text = visionText.text
                        println("Nilai visionText " + visionText.text)
                    }
                    .addOnFailureListener { e ->
                    }
            }

        }

        else{
            Toast.makeText(activity, "Please select photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
            }
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri :Uri ?= UCrop.getOutput(data!!)
            setImage(resultUri!!)
            finalUri=resultUri
            binding.buttonDetect.visibility= View.VISIBLE
            binding.buttonCancel.visibility=View.VISIBLE
            binding.buttonPhoto.visibility=View.GONE
            binding.buttonGallery.visibility=View.GONE
        }
    }

    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options:UCrop.Options=UCrop.Options()


        context?.let {
            UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(activity?.cacheDir,destination)))
                .withOptions(options)
                .withAspectRatio(0F, 0F)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000)
                .start(it, this)
        }
    }

    private fun checkPermission(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionPermit() {
        val listPermission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        requestPermissions(listPermission, 100)
    }

}