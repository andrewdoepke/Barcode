package com.example.barcodevalidator

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.os.bundleOf
import com.example.barcodevalidator.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding
private var state: Bundle = Bundle()

class MainActivity : AppCompatActivity(){

    private lateinit var currBarcode: Barcode

    private var currAlgo = ""
    private var barCodes: ArrayList<Barcode> = ArrayList() //store barcodes. Last index will always be current
    lateinit var currIdNoTV: EditText
    lateinit var currCheckDTV: EditText
    lateinit var codeDisp: TextView
    lateinit var imgDisp: ImageView
    lateinit var spinV: Spinner
    lateinit var spinA: Spinner
    private var imgSt: Int = 1


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("b_codeDisp", codeDisp.text.toString())
        outState.putInt("b_imgDisp", imgSt)

    }
/*
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currIdNoTV = binding.editTextNumber
        currCheckDTV = binding.editTextNumber2

        codeDisp = binding.fullCode
        codeDisp.text = ""

        if (savedInstanceState != null) {
            codeDisp.text = savedInstanceState.get("b_codeDisp") as String
        }

    imgDisp = binding.image
    imgDisp.visibility = View.INVISIBLE

    if(savedInstanceState != null){
        imgSt = savedInstanceState.get("b_imgDisp") as Int
        imgDisp.visibility = View.VISIBLE
    }

        if(imgSt == 1){
            imgDisp.setImageResource(R.drawable.invalid)
        } else {
            imgDisp.setImageResource(R.drawable.valid)
        }


//------------------------------------------------------------------------------------------------------------------------//
        //Set First Spinner
        //Method
        val vG = resources.getStringArray(R.array.VorG)
        spinV = binding.spinVorG
        val adapV = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vG)
        spinV.adapter = adapV
        spinV.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Test
                    //Toast.makeText(this@MainActivity, vG[position], Toast.LENGTH_SHORT).show()
                    when(position) {
                        0 -> {
                            binding.editTextNumber2.visibility = View.VISIBLE
                            binding.textView3.visibility = View.VISIBLE
                            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                                currIdNoTV.imeOptions = EditorInfo.IME_ACTION_NEXT
                            } else {
                                currIdNoTV.imeOptions = EditorInfo.IME_ACTION_DONE
                            }
                        }
                        1 -> {
                            binding.editTextNumber2.visibility = View.INVISIBLE
                            binding.textView3.visibility = View.INVISIBLE
                            currCheckDTV.text.clear() //clear check digit
                            currIdNoTV.imeOptions = EditorInfo.IME_ACTION_DONE
                        }
                    }


                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        //------------------------------------------------------------------------------------------------------------------------//
        //Set Second Spinner
        val vA = resources.getStringArray(R.array.AlgoTypes)
        spinA = binding.spinAlgo
        val adapA = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vA)
        spinA.adapter = adapA

        spinA.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Test
                //Toast.makeText(this@MainActivity, vG[position], Toast.LENGTH_SHORT).show()
                when(position){
                    0 -> {
                        currAlgo = "UPC"
                    }
                    1 -> {
                        currAlgo = "ISBN13"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        //------------------------------------------------------------------------------------------------------------------------//

        //set listener for buttons
        binding.button.setOnClickListener {
            //Toast.makeText(applicationContext,"button works!",Toast.LENGTH_SHORT).show()
            if(currCheckDTV.visibility == View.VISIBLE && currCheckDTV.text.toString().trim() == ""){
                currCheckDTV.setText("-1")
                Toast.makeText(this@MainActivity, "Please Enter a Check Digit", Toast.LENGTH_SHORT).show()
            }

            if(currIdNoTV.text.toString().trim() == ""){
                currCheckDTV.setText("-1")
                Toast.makeText(this@MainActivity, "Please Enter a Code Body", Toast.LENGTH_SHORT).show()
            }

            currBarcode = Barcode(currIdNoTV.text.toString(), currCheckDTV.text.toString(), currAlgo)
            barCodes.add(currBarcode) //make barcode obj from this and add it to our arraylist

            //clear text fields
            currIdNoTV.text.clear()
            currCheckDTV.text.clear()

            //set text to barcode
            codeDisp.text = currBarcode.fullCode()

            //make image visible
            imgDisp.visibility = View.VISIBLE

            //Draw Barcode and display information
            imgSt = if(!currBarcode.getValidity()){
                imgDisp.setImageResource(R.drawable.invalid)
                1
            } else {
                imgDisp.setImageResource(R.drawable.valid)
                2
            }
        }

        binding.reset.setOnClickListener { //clear all fields
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}