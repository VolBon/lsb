package com.example.bonetvol.lsbdecks

import android.icu.text.DecimalFormat
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    internal val perLbSea = 1.19
    internal val perLbAir = 2.89
    internal var airDelivery = false
    internal val doorDelivery = 20.0
    internal var priceBlank = 14.5
    internal var priceWithGraphics: Double = 0.toDouble()
    internal var quantity: Int = -1
    internal var colors: Int? = 0
    internal var kurs: Double? = 27.0
    //internal var eachText:TextView? = null!!
    //internal var totalText:TextView? = null!!
    //internal var rate:TextView? = null!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getting


        //Log.i("Finally", String.valueOf(currencyRate));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun calculate() {
        try {
            colors = Integer.parseInt((findViewById(R.id.colorsNumber) as EditText).text.toString())
        } catch (e: Exception) {
            colors = 0
        }

        try {
            quantity = Integer.parseInt((findViewById(R.id.quantity) as EditText).text.toString())
        } catch (e: Exception) {
            Toast.makeText(applicationContext, String.format("Please provide data!"), Toast.LENGTH_LONG).show()
        }

        //check if air delivery was chosen
        val delivery = findViewById(R.id.toggler) as ToggleButton
        airDelivery = if (delivery.isChecked) true else false
        //Log.i("AIR", String.valueOf(airDelivery));


        //each color setup price
        val setUpColor = (if (quantity < 50) 35 else 30).toDouble()

        //each deck price with printing or blank
        if (colors !== 0) {
            priceWithGraphics = priceBlank + setUpColor * colors!! / quantity + colors!! * 0.5 + 0.5
        } else {
            priceWithGraphics = priceBlank
        }

        if (quantity !== -1) {
            val deliveryCost = deliveryCalculator(quantity, airDelivery)
            val priceWithDelivery = priceWithGraphics + deliveryCost / quantity

            val priceInUah = java.lang.Double.parseDouble(DecimalFormat("####.##").format(priceWithDelivery * kurs!!))
            val totalAll = java.lang.Double.parseDouble(DecimalFormat("####.##").format(priceWithDelivery * quantity))

            var eachText = this.findViewById(R.id.resultEach) as TextView
            var totalText = this.findViewById(R.id.resultTotal) as TextView
            eachText!!.text = priceInUah.toString()
            totalText!!.text = totalAll.toString()
        }
    }

    fun deliveryCalculator(quantity: Int, airDelivery: Boolean): Double {
        val boxesTwenty: Int
        var boxesTen = 0
        if (quantity % 20 == 0) {
            boxesTwenty = quantity / 20
        } else if (quantity % 20 > 10) {
            boxesTwenty = quantity / 20 + 1
        } else {
            boxesTwenty = quantity / 20
            boxesTen = 1
        }
        Log.i("BOXES", boxesTen.toString() + " and " + boxesTwenty.toString())
        if (!airDelivery) {
            return (perLbSea * 84 + doorDelivery) * boxesTwenty + (perLbSea * 42 + doorDelivery) * boxesTen
        } else {
            return (perLbAir * 84 + doorDelivery) * boxesTwenty + (perLbAir * 42 + doorDelivery) * boxesTen
        }
    }

    fun getCurrencyRates() {
        var rate = this.findViewById(R.id.textView5) as TextView
        rate.text = kurs.toString()
        getCurrencyRates()
        kurs = java.lang.Double.valueOf(rate.text.toString())
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://apilayer.net/api/live?access_key=a158456551a829bcfa8fa137341e071e&currencies=UAH&format=1"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    //mTextView.setText("Response is: "+ response.substring(0,205));
                    //Log.i("Response", response);
                    val jsoni = java.lang.Double.valueOf(response.substring(190, 195))// + Integer.valueOf(response.substring(193, 195))/100;
                    //Log.i("HERE", String.valueOf(jsoni));
                    rate!!.text = jsoni.toString()
                }, Response.ErrorListener { Log.i("That didn't work!", "no") })
        queue.add(stringRequest)
    }

    companion object {
        val apishechka = "http://apilayer.net/api/live?access_key=a158456551a829bcfa8fa137341e071e&currencies=UAH&format=1"
    }
}