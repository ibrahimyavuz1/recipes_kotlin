package com.example.recipes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_recipe.*
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest


class   RecipeFragment : Fragment() {
    var chosenImage : Uri?=null
    var chosenBitmap: Bitmap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            save(it)
        }
        imageView.setOnClickListener {
            saveImage(it)
        }
        arguments?.let{
            var incomingData= RecipeFragmentArgs.fromBundle(it).naviData
            if(incomingData.equals("clicked from menu")){
                eatNameText.setText("")
                eatIngredientsText.setText("")
                saveButton.visibility=View.VISIBLE
                val imageSelectBackground= BitmapFactory.decodeResource(context?.resources,R.drawable.photo)
                imageView.setImageBitmap(imageSelectBackground)
            }
            else{
                saveButton.visibility=View.INVISIBLE
                val chosenId= RecipeFragmentArgs.fromBundle(it).id
                context?.let {
                    try {
                        val db= it.openOrCreateDatabase("Eats",Context.MODE_PRIVATE,null)
                        val cursor= db.rawQuery("SELECT * FROM Eats WHERE id=?", arrayOf(chosenId.toString()))
                        val eatNameIndex= cursor.getColumnIndex("eatname")
                        val eatIngredientsIndex= cursor.getColumnIndex("eatingredients")
                        val eatImageIndex=cursor.getColumnIndex("image")
                        while (cursor.moveToNext()){
                            eatNameText.setText(cursor.getString(eatNameIndex))
                            eatIngredientsText.setText(cursor.getString(eatIngredientsIndex))
                            val byteArray=cursor.getBlob(eatImageIndex)
                            val bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }
                    catch (e:Exception){
                        e.printStackTrace()

                    }

                }
            }
        }

    }
    fun save(view: View){
        val eatName=eatNameText.text.toString()
        val eatIngredients=eatIngredientsText.text.toString()
        if(chosenBitmap!=null){
            val smallBitmap= createSmallBitmap(chosenBitmap!!,300)
            val outputStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray=outputStream.toByteArray()
            try {
                context?.let {
                    val database=it.openOrCreateDatabase("Eats", Context.MODE_PRIVATE,null)
                    database.execSQL(  "CREATE TABLE IF NOT EXISTS Eats(id INTEGER PRIMARY KEY,eatname VARCHAR,eatingredients VARCHAR,image BLOB)")
                    val sqlstring="INSERT INTO Eats (eatname, eatingredients,image) VALUES(?,?,?)"
                    val statement= database.compileStatement(sqlstring)
                    statement.bindString(1,eatName)
                    statement.bindString(2,eatIngredients)
                    statement.bindBlob(3,byteArray)
                    statement.execute()
                }
            }
            catch (e:Exception){
                e.printStackTrace()

            }
            val action= RecipeFragmentDirections.actionRecipeFragmentToRecipesListFragment()
            Navigation.findNavController(view).navigate(action)
        }

        println("save function")
    }
    fun saveImage(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

            }
            else{
                val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)


            }
        }
        println("saveImage function")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if (grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==2&&resultCode== Activity.RESULT_OK&&data!=null){
            chosenImage=data.data
            try {
                context?.let {
                    if(chosenImage!=null){
                        if(Build.VERSION.SDK_INT>=28){
                            var source =ImageDecoder.createSource(it.contentResolver,chosenImage!!)
                            chosenBitmap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(chosenBitmap)
                        }
                        else{
                            chosenBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,chosenImage)
                            imageView.setImageBitmap(chosenBitmap)
                        }
                    }
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun createSmallBitmap(userselectedbitmap: Bitmap,maxsize:Int):Bitmap{
        var width=userselectedbitmap.width
        var height = userselectedbitmap.height
        val bitmaprate:Double=width.toDouble()/height.toDouble()
        if(bitmaprate>1){
            width=maxsize
            val shortenedheight= width/bitmaprate
            height=shortenedheight.toInt()
        }
        else{
            height=maxsize
            val shortenedwidth=height*bitmaprate
            width=shortenedwidth.toInt()

        }
        return Bitmap.createScaledBitmap(userselectedbitmap,width,height,true)
    }

}