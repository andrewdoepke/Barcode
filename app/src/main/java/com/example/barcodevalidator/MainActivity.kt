package com.example.barcodevalidator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.barcodevalidator.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var currBarcode: Barcode

    private var currAlgo = ""
    private var barCodes: ArrayList<Barcode> = ArrayList() //store barcodes. Last index will always be current


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currIdNoTV = binding.editTextNumber
        val currCheckDTV = binding.editTextNumber2

        val codeDisp = binding.fullCode

        val imgDisp = binding.image
//------------------------------------------------------------------------------------------------------------------------//
        //Set First Spinner
        //Method
        val vG = resources.getStringArray(R.array.VorG)
        val spinV = binding.spinVorG
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
                            currIdNoTV.imeOptions = EditorInfo.IME_ACTION_NEXT
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
        val spinA = binding.spinAlgo
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
            if(!currBarcode.getValidity()){
                imgDisp.setImageResource(R.drawable.invalid)
            } else {
                imgDisp.setImageResource(R.drawable.valid)
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