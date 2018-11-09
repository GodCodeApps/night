package com.night

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.RelativeLayout
import java.util.*

/**
 *Peng YanMing on 2018\11\9 0009
 */
class StartView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val TAG = StartView::class.java.simpleName

    /**
     * view宽
     */
    private var mWidth = 0

    /**
     * view高度
     */
    private var mHeight = 0

    /**
     * 小球最小移动速度
     */
    private val mMinSpeed = 1

    /**
     * 小球最大移动速度
     */
    private val mMaxSpeed = 5

    /**
     * 默认圆点x
     */
    private var mDefaultRadiusX: Float = 0.toFloat()

    /**
     * 默认圆点y
     */
    private var mDefaultRadiusY: Float = 0.toFloat()
    private val mRandom: Random
    private val mCoordinate = Coordinate()

    init {
        mRandom = Random()

        val speedX = (mRandom.nextInt(mMaxSpeed - mMinSpeed + 1) + 5) / 70f
        val speedY = (mRandom.nextInt(mMaxSpeed - mMinSpeed + 1) + 5) / 70f
        mCoordinate.vx = if (mRandom.nextBoolean()) speedX else -speedX
        mCoordinate.vy = if (mRandom.nextBoolean()) speedY else -speedY

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm?.defaultDisplay?.getMetrics(outMetrics)

        mWidth = resources.getDimensionPixelSize(R.dimen.sm_px_80)
        mHeight = resources.getDimensionPixelSize(R.dimen.sm_px_80)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth, mHeight)
        mCoordinate.radius = mWidth / 2
        mCoordinate.cx = x + mWidth / 2
        mCoordinate.cy = y + mHeight / 2
        mDefaultRadiusX = mCoordinate.cx
        mDefaultRadiusY = mCoordinate.cy
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startTime = System.currentTimeMillis()

        changeSpeed(mCoordinate) // 碰撞边界的计算
        mCoordinate.move() // 移动

        x = mCoordinate.cx - width / 2
        y = mCoordinate.cy - height / 2

        val stopTime = System.currentTimeMillis()
        val runTime = stopTime - startTime
        postInvalidateDelayed(Math.abs(runTime - 16))
    }

    /**
     * 记录当前坐标信息
     */
    inner class Coordinate {
        /**
         * 半径
         */
        var radius: Int = 0

        /**
         * 圆心x
         */
        var cx: Float = 0.toFloat()

        /**
         * 圆心y
         */
        var cy: Float = 0.toFloat()

        /**
         * X轴速度
         */
        var vx: Float = 0.toFloat()

        /**
         * Y轴速度
         */
        var vy: Float = 0.toFloat()

        // 移动
        fun move() {
            //向角度的方向移动，偏移圆心
            cx += vx
            cy += vy
        }

        fun left(): Int {
            return (cx - radius).toInt()
        }

        fun right(): Int {
            return (cx + radius).toInt()
        }

        fun bottom(): Int {
            return (cy + radius).toInt()
        }

        fun top(): Int {
            return (cy - radius).toInt()
        }
    }

    /**
     * 处理星球碰撞边界改变速度
     *
     * @param ball
     */
    fun changeSpeed(ball: Coordinate) {
        val left = (mDefaultRadiusX - (mWidth / 2).toFloat() - MAX_MOVE_RANGE.toFloat()).toInt()
        val top = (mDefaultRadiusY - (mHeight / 2).toFloat() - MAX_MOVE_RANGE.toFloat()).toInt()
        val right = (mDefaultRadiusX + (mWidth / 2).toFloat() + MAX_MOVE_RANGE.toFloat()).toInt()
        val bottom = (mDefaultRadiusY + (mHeight / 2).toFloat() + MAX_MOVE_RANGE.toFloat()).toInt()

        val speedX = ball.vx
        val speedY = ball.vy

        //碰撞边界处理速度
        if (ball.left() <= left && speedX < 0) {
            ball.vx = -ball.vx
        } else if (ball.top() <= top && speedY < 0) {
            ball.vy = -ball.vy
        } else if (ball.right() >= right && speedX > 0) {
            ball.vx = -ball.vx
        } else if (ball.bottom() >= bottom && speedY > 0) {
            ball.vy = -ball.vy
        }
    }

    companion object {
        /**
         * 上下左右浮动偏移量
         */
        private val MAX_MOVE_RANGE = 8//四周最大移动范围
    }
}
