package com.example.barcodevalidator

import java.io.OutputStream

class Barcode (code: String, type: String, isComplete: Boolean){
    var code = code
    var type = type
    var theBeef: String = ""
    var checkD: Int = -1
    var isComplete = isComplete
    var isValid: Boolean = false

    init {
        if(!this.isComplete){ //if it's not a complete code, generate the check digit and validate it
            theBeef = this.code
            checkD = generateCD()
            this.code = theBeef + checkD
            isValid = validate()
            this.isComplete = true
        } else { //this is a complete code
            theBeef = code.substring(0, code.length-1)
            checkD = code[code.length - 1].digitToInt()
            isValid = validate()
            //isComplete is already true
            //code is already full
        }
    }

    override fun toString(): String {
        return "Barcode: " + code + "\n" +
                "Type: " + type + "\n" +
                "Is Complete? " + isComplete + "\n" +
                "Is Valid? " + isValid + "\n" +
                "Barcode Body: " + theBeef + "\n" +
                "Check Digit: " + checkD
    }


    private fun validate(): Boolean {
        if(checkD < 0) { //covers cases where an incorrect length is passed or otherwise error
            return false;
        }
        return checkD == generateCD() //generate check dig and test if current is stored
    }

    private fun generateCD(): Int {
        when (type) {
            "UPC" -> {
                return upcCD()
            }
            "ISBN13" -> {
                return isbn13CD()
            }
        }
        return -1
    }

    private fun isbn13CD(): Int {
        var dig: Int = -1
        var runningTotal: Int = 0
        val fInd: Int = theBeef.length - 1 //final index of code
        val numbers = theBeef.map { it.digitToInt() }.toTypedArray() //each number minus the check digit

        if(theBeef.length != 12){
            return -1 //invalid
        }

        for(i in 0..fInd){
            runningTotal += if(i % 2 == 0){
                numbers[i]
            } else {
                numbers[i] * 3
            }
        }

        dig = runningTotal % 10

        return if(dig == 0){ //if 0, return 0
            dig
        } else { //otherwise return 10 - number
            10 - dig
        }


        //something broke
        return -1
    }

    private fun upcCD(): Int{
        var dig: Int = -1
        var runningTotal: Int = 0
        val fInd: Int = theBeef.length //final index of code
        val numbers = theBeef.map { it.digitToInt() }.toTypedArray() //each number minus the check digit

        for(i in 1..fInd) { //Step 1
            val arrInd: Int = i-1
            if(i % 2 != 0){ //if number is odd
                runningTotal += numbers[arrInd]
            }
        }
        dig = runningTotal * 3

        runningTotal = 0 //reset

        for(i in 1..fInd){ //Step 2
            val arrInd: Int = i-1
            if(i%2 == 0){ //if number is even
                runningTotal += numbers[arrInd]
            }
        }
        dig += runningTotal

        //Step 3
        dig %= 10 //modulo

        return if(dig == 0){ //if 0, return 0
            dig
        } else { //otherwise return 10 - number
            10 - dig
        }

        //something broke
        return -1

    }
}