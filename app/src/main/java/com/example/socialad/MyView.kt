package com.example.socialad

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class MyView(context: Context?) : View(context), View.OnTouchListener {

    var dx = 0f //Distance among vertical lines
    var dy = 0f //Distance among horizontal lines

    val vLines = 3f
    val hLines = 3f

    val mPaint = Paint().apply{
        style=Paint.Style.FILL_AND_STROKE
        color= Color.parseColor("#AA0000AA")
        textSize=100f
        strokeWidth = 30f;
    }

    val blackPaint = Paint().apply {
        style=Paint.Style.FILL_AND_STROKE
        color= Color.BLACK
        strokeWidth=2f
        textSize=50f
    }

    val redPaint = Paint().apply {
        style=Paint.Style.FILL_AND_STROKE
        color= Color.RED
        strokeWidth=2f
        textSize=50f
    }

    init {
        setOnTouchListener(this)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dx=width/(vLines+1) ///i want 3 lines but 4 cell
        dy=(height /3).toFloat()/hLines

        var rr= Rect()

        //Place texts
        var i = 0
        for ( t in arrayOf("Color","Hair","H-Color")){
            blackPaint.getTextBounds(t,0,t.length ,rr)
            var offy = (rr.top - rr.bottom) / 2
            var offx = (rr.right - rr.left) / 2
            canvas.drawText(t,
                dx/2-offx,
                i*dy +dy/2-offy,
                redPaint)
            i++

        }

        //place circle colors
        i = 0
        for (t in arrayOf(Color.MAGENTA, Color.YELLOW, Color.DKGRAY)) {
            canvas.drawRect(i*dx+dx, 0f, i*dx + 2*dx, dy, Paint().apply{
                style=Paint.Style.FILL_AND_STROKE
                color= t
                strokeWidth=2f
            })
            i++
        }

        //place hair color
        i = 0
        for (t in arrayOf(Color.YELLOW, Color.BLACK, Color.RED)) {
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

        /*canvas.drawCircle((width /2).toFloat(), (height /2).toFloat(), 100F, mPaint);
        canvas.drawRect(
            (left +(right - left)/3).toFloat(),
            (top +(bottom - top)/3).toFloat(),
            (right -(right - left)/3).toFloat(),
            (bottom -(bottom - top)/3).toFloat(), mPaint);*/
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return true
    }

}