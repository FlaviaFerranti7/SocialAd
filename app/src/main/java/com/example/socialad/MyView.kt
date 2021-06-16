package com.example.socialad

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat


class MyView(context: Context?) : View(context), View.OnTouchListener {

    var dx = 0f //Distance among vertical lines
    var dy = 0f //Distance among horizontal lines

    var radius = 100F //grandezza avatar

    val vLines = 3f
    val hLines = 3f

    var changeColor = false;
    var changeHair = false;
    var changeHColor = false;

    var position = 0;
    var lastHairSave = 5;
    var lastColorSave = 1;
    var lastHCSave = 9;

    val mPaint = Paint().apply{
        style=Paint.Style.FILL_AND_STROKE
        color= Color.parseColor("#FF6200EE")
        textSize=100f
        strokeWidth = 30f;
    }

    val blackPaint = Paint().apply {
        style=Paint.Style.FILL_AND_STROKE
        color= Color.BLACK
        strokeWidth=2f
        textSize=50f
    }

    val textPaint = Paint().apply {
        style=Paint.Style.FILL_AND_STROKE
        strokeWidth=2f
        textSize=50f
    }

    val hairCPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context!!, R.color.h_yellow)
        strokeWidth = 2f
        textSize = 50f
    }

    val facePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context!!, R.color.f_pink)
        strokeWidth = 2f
        textSize = 50f
    }

    init {
        isSaveEnabled = true;
        setOnTouchListener(this);
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dx=width/(vLines+1) ///i want 3 lines but 4 cell
        dy=(height /3).toFloat()/hLines

        var rr= Rect()

        //Place texts
        textPaint.setColor(Color.RED);
        var i = 0
        for ( t in arrayOf("Color","Hair","H-Color")){
            blackPaint.getTextBounds(t,0,t.length ,rr)
            var offy = (rr.top - rr.bottom) / 2
            var offx = (rr.right - rr.left) / 2
            canvas.drawText(t,
                dx/2-offx,
                i*dy +dy/2-offy,
                textPaint)
            i++

        }

        //place circle colors
        i = 0
        for (t in arrayOf(ContextCompat.getColor(context, R.color.f_pink) , ContextCompat.getColor(context, R.color.f_yellow), ContextCompat.getColor(context, R.color.f_brown)) ){
            canvas.drawRect(i*dx+dx, 0f, i*dx + 2*dx, dy, Paint().apply{
                style=Paint.Style.FILL_AND_STROKE
                color= t
                strokeWidth=2f
            })
            i++
        }

        //place hair color
        i = 0
        for (t in arrayOf(ContextCompat.getColor(context, R.color.h_yellow), ContextCompat.getColor(context, R.color.h_black), ContextCompat.getColor(context, R.color.h_red))) {
            canvas.drawRect(i*dx+dx, 2*dy, i*dx + 2*dx, 2*dy+dy, Paint().apply{
                style=Paint.Style.FILL_AND_STROKE
                color= t
                strokeWidth=2f
            })
            i++
        }

        //place hair shape
        i = 1
        for (t in arrayOf("Triang", "Square", "Rect")) {
            blackPaint.getTextBounds(t,0,t.length ,rr)
            var offy = (rr.top - rr.bottom) / 2
            var offx = (rr.right - rr.left) / 2
            canvas.drawText(t,
                i*dx +dx/2-offx,
                dy +dy/2-offy,
                blackPaint)
            i++
        }

        //Draw  horizontal lines
        for (y in 1..hLines.toInt()){
            canvas.drawLine(
                0f,y*dy,
                width.toFloat(),y*dy,
                blackPaint)
        }
        //Draw vertical lines
        for (x in 1..vLines.toInt()){
            canvas.drawLine(
                x*dx,0f,
                x*dx,hLines*dy,
                blackPaint)
        }

        //place Avatar
        if(changeHColor){
            if(position==9){ hairCPaint.color = ContextCompat.getColor(context, R.color.h_yellow); lastHCSave = position;}
            else if (position == 10){ hairCPaint.color = ContextCompat.getColor(context, R.color.h_black); lastHCSave = position;}
            else{ hairCPaint.color = ContextCompat.getColor(context, R.color.h_red); lastHCSave = position}
            changeHColor = false
        }

        if(changeHair){
            if(position==5) lastHairSave = 5
            else if (position == 6) lastHairSave = 6
            else lastHairSave = 7
            changeHair = false
        }
        if(lastHairSave == 5) drawTriangle(canvas, hairCPaint, width/2, (height/1.6 - radius).toInt(), (2*radius).toInt()); // hair triangle
        else if(lastHairSave == 6) canvas.drawRect(width/2 - radius, (height/1.6 - (radius + radius/2)).toFloat(), width/2 + radius, (height /1.6).toFloat(), hairCPaint); //hair square
        else if (lastHairSave == 7) canvas.drawRect(width/2 - radius, (height/1.6 - (radius + radius/4)).toFloat(), width/2 + radius, (height /1.6 + 2*radius).toFloat(), hairCPaint); //hair rectangle

        if(changeColor){
            if(position==1){ facePaint.color = ContextCompat.getColor(context, R.color.f_pink); lastColorSave = position;}
            else if (position == 2){ facePaint.color = ContextCompat.getColor(context, R.color.f_yellow); lastColorSave = position;}
            else { facePaint.color = ContextCompat.getColor(context, R.color.f_brown); lastColorSave = position;}
            changeColor = false
        }

        canvas.drawCircle((width /2).toFloat(), (height /1.6).toFloat(), radius, facePaint);    //face

        drawRhombus(canvas, blackPaint, (width /2), (height /1.6 + radius/4).toInt(), (radius/8).toInt());  //nose
        canvas.drawCircle(width /2- radius/2, (height /1.6 - radius/4).toFloat(), radius/12, blackPaint); //left eye
        canvas.drawCircle(width /2+ radius/2, (height /1.6 - radius/4).toFloat(), radius/12, blackPaint); //right eye

        //place Save button
        canvas.drawRect((2.5*dx).toFloat(), (7.5*dy).toFloat(), (2.5*dx+dx).toFloat(), (7.5*dy+dy).toFloat(), mPaint);
        blackPaint.getTextBounds("Save",0,4 ,rr)
        var offy = (rr.top - rr.bottom) / 2
        var offx = (rr.right - rr.left) / 2
        textPaint.setColor(Color.WHITE);
        canvas.drawText("Save",
                (2.5*dx).toFloat() +dx/2-offx,
                (7.5*dy).toFloat() +dy/2-offy,
                textPaint);

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = (event.x / dx).toInt()
                val y = (event.y / dy).toInt()
                var n = (4* y) + x

                //Toast.makeText(context, "" + n, Toast.LENGTH_SHORT).show()
                when (n) {
                    1, 2, 3 ->{
                        changeColor = true; position = n
                    }
                    5, 6, 7 ->{
                        changeHair = true; position = n
                    }
                    9, 10, 11 ->{
                        changeHColor = true; position = n
                    }
                    30, 31, 34, 35 ->{
                        val photo= "" + lastColorSave + "" + lastHairSave + "" + lastHCSave
                        //Toast.makeText(context, photo, Toast.LENGTH_SHORT).show()
                        SendUserToSetupActivity(photo);

                    }
                }

                invalidate()

            }
        }
        return true
    }

    fun drawTriangle(canvas: Canvas, paint: Paint?, x: Int, y: Int, width: Int) {
        val halfWidth = width / 2
        val path = Path()
        path.moveTo(x.toFloat(), (y - halfWidth).toFloat()) // Top
        path.lineTo((x - halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom left
        path.lineTo((x + halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom right
        path.lineTo(x.toFloat(), (y - halfWidth).toFloat()) // Back to Top
        path.close()
        canvas.drawPath(path, paint!!)
    }

    fun drawRhombus(canvas: Canvas, paint: Paint?, x: Int, y: Int, width: Int) {
        val halfWidth = width / 2
        val path = Path()
        path.moveTo(x.toFloat(), (y + halfWidth).toFloat()) // Top
        path.lineTo((x - halfWidth).toFloat(), y.toFloat()) // Left
        path.lineTo(x.toFloat(), (y - halfWidth).toFloat()) // Bottom
        path.lineTo((x + halfWidth).toFloat(), y.toFloat()) // Right
        path.lineTo(x.toFloat(), (y + halfWidth).toFloat()) // Back to Top
        path.close()
        canvas.drawPath(path, paint!!)
    }

    private fun SendUserToSetupActivity(value : String) {
        val setupIntent = Intent(context, SetupActivity::class.java);
        setupIntent.putExtra("photo", value);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(setupIntent);
    }

}