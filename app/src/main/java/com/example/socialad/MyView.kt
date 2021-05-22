package com.example.socialad

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class MyView(context: Context?) : View(context), View.OnTouchListener {

    var dx = 0f //Distance among vertical lines
    var dy = 0f //Distance among horizontal lines

    var radius = 100F //grandezza avatar

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

    val basePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2f
        textSize = 50f
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

        //place Avatar
        basePaint.setColor(Color.YELLOW);
        //canvas.drawRect(width/2 - radius, (height/1.6 - (radius + radius/2)).toFloat(), width/2 + radius, (height /1.6).toFloat(), basePaint); //hair square
        //canvas.drawRect(width/2 - radius, (height/1.6 - (radius + radius/4)).toFloat(), width/2 + radius, (height /1.6 + 2*radius).toFloat(), basePaint); //hair rectangle
        drawTriangle(canvas, basePaint, width/2, (height/1.6 - radius).toInt(), (2*radius).toInt()); // hair triangle

        basePaint.setColor(Color.MAGENTA);
        canvas.drawCircle((width /2).toFloat(), (height /1.6).toFloat(), radius, basePaint);    //face
        basePaint.setColor(Color.BLACK);
        drawRhombus(canvas, basePaint, (width /2), (height /1.6 + radius/4).toInt(), (radius/8).toInt());  //noise
        canvas.drawCircle(width /2- radius/2, (height /1.6 - radius/4).toFloat(), radius/12, basePaint);
        canvas.drawCircle(width /2+ radius/2, (height /1.6 - radius/4).toFloat(), radius/12, basePaint);

        //place Save button
        canvas.drawRect((2.5*dx).toFloat(), (7.5*dy).toFloat(), (2.5*dx+dx).toFloat(), (7.5*dy+dy).toFloat(), mPaint);
        blackPaint.getTextBounds("Save",0,4 ,rr)
        var offy = (rr.top - rr.bottom) / 2
        var offx = (rr.right - rr.left) / 2
        redPaint.setColor(Color.WHITE);
        canvas.drawText("Save",
                (2.5*dx).toFloat() +dx/2-offx,
                (7.5*dy).toFloat() +dy/2-offy,
                redPaint);
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
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

}