package com.example.ballrebound

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var ballList: MutableList<Ball>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ballList = mutableListOf<Ball>()

        activity_main_btn_add.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var mBall = Ball(this@MainActivity)
                ballList.add(mBall)
            }
        })
        activity_main_btn_end.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                for(i in 0 until ballList.size) {
                    ballList[i].stop()
                }
            }
        })
        activity_main_btn_start.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                for(i in 0 until ballList.size) {
                    ballList[i].start()
                }
            }
        })
        activity_main_btn_clear.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                for(i in 0 until ballList.size) {
                   var  relative = findViewById<RelativeLayout>(R.id.activity_main_relativelayout)
                    relative!!.removeView(ballList[i].imageView)
                    ballList[i].mThread.quitSafely()
                }
                ballList.clear()

            }
        })
    }

    companion object {
        @kotlin.jvm.JvmField
        var MOVE_IMAGE: Int = 1
    }

    class Ball(var context:Context) {
        var handler: Handler? = null
        var MOVE_IMAGE = 1
        //初始位置
        var orginX=Random.nextInt(0,200)
        var orginY=Random.nextInt(0,200)
        // 移動方向和距離
        var decX = Random.nextInt(1,5)
        var decY = Random.nextInt(1,5)
        // 座標
        var moveX: Int = 0
        var moveY: Int = 0
        var isMove: Boolean = false// 是否正在移動
        var relative: RelativeLayout? = null
        var imageView: ImageView? = null
        var mThread:HandlerThread = HandlerThread("BallThread")
        var ballHandler = Handler()
        var ballRunnable: Runnable = Runnable {
            while (this.isMove) {
                moveX += decX;
                moveY += decY;
                if ((moveX + imageView!!.getWidth()) >= relative!!.getWidth()  ) { decX = -decX; }
                if(moveX < 0){ decX = -decX; }
                if ((moveY + imageView!!.getHeight() ) >= relative!!.getHeight()) { decY = -decY }
                if (moveY < 0) { decY = -decY }
                var message = Message();
                message.what = MOVE_IMAGE;

                var bundle = Bundle();
                bundle.putInt("moveX", moveX);
                bundle.putInt("moveY", moveY);
                message.setData(bundle);
                (handler as MyHandler).sendMessage(message);
                try {
                    Thread.sleep(10);
                } catch (e: InterruptedException) {
                    e.printStackTrace();
                }
            }
        }
        var boundRate: Float = 0F
        init {
            moveX=orginX
            moveY=orginY
            imageView = ImageView(context!!)
            imageView!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_soccer_ball))
            var mLayoutParams = RelativeLayout.LayoutParams(
            50,50
            )
            mLayoutParams.setMargins(orginX, 10, 0,orginY)
            imageView!!.layoutParams = mLayoutParams
            relative = (context as MainActivity).findViewById<RelativeLayout>(R.id.activity_main_relativelayout)
            relative!!.addView(imageView)
            setColor(imageView!!)
            handler = MyHandler(context as MainActivity,imageView)
            mThread.start()
            ballHandler = Handler(mThread.getLooper())
        }
        fun setColor(mImageView:ImageView){
            var r= Random.nextInt(0,255)
            var g= Random.nextInt(0,255)
            var b= Random.nextInt(0,255)
            var mColor = Color.rgb(r,g,b)
            val bottomLayer = ColorDrawable(mColor) as Drawable
            val topLayer = context.resources.getDrawable(R.drawable.ic_soccer_ball,null)
            val playWithBackground = LayerDrawable(arrayOf(bottomLayer, topLayer))
            Glide.with(context)
                .load(playWithBackground)
                .apply(RequestOptions.circleCropTransform())
                .into(mImageView)
        }
        fun start() {
            this.isMove = false;
             if (!this.isMove) {
                 this.isMove = true;
             } else {
                 return;
             }
            ballHandler.post(ballRunnable)

        }
        fun stop(){
            this.isMove=false
            ballHandler.removeCallbacks(ballRunnable)
        }
    }
}
