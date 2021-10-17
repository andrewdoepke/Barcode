package com.example.barcodevalidator

class Barcode (idNo: String, checkDig: String, type: String){
    private var theBeef: String = idNo //first part of barcode
    private var type = type
    private var checkD: Int = -1
    private var code: String = theBeef
    private var isValid: Boolean = false

    init {
        if(checkDig.trim() == ""){ //no check digit passed, so generate one
            this.checkD = generateCD()
        } else {
            this.checkD = checkDig.toInt()

            if(this.checkD !in 0..9){ //needs to be a single digit
                checkD = -1 //err value
            }
        }

        this.code += checkD //add check digit to code
        isValid = validate() //validate code
    }

    override fun toString(): String {
        return "Barcode: " + code + "\n" +
                "Type: " + type + "\n" +
                "Is Valid? " + isValid + "\n" +
                "Barcode Body: " + theBeef + "\n" +
                "Check Digit: " + checkD
    }

    fun getValidity(): Boolean{
        return isValid
    }

    fun fullCode(): String{
        return code
    }


    private fun validate(): Boolean {
        val tCD = generateCD()

        if(tCD < 0) { //covers cases where an incorrect length is passed or otherwise error
            return false;
        }

        if(theBeef == ""){
            return false
        }

        return checkD == tCD //generate check dig and test if current is stored
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

    }
}