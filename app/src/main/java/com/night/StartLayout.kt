package com.night

/**
 *Peng YanMing on 2018\11\9 0009
 */

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import java.math.BigDecimal

/**
 * Peng YanMing on 2018\11\8 0008
 */
class StartLayout : RelativeLayout {
    var yDown: Float = 0.toFloat()
    var xDown: Float = 0.toFloat()
    private var paint: Paint? = null
    private var path: Path? = null
    private var mposX: Float = 0.toFloat()
    private var mposY: Float = 0.toFloat()
    private var rect: Rect? = null
    private val LEFT_POIN = 200

    /**
     * 保存子元素x坐标
     */
    private val xs = IntArray(7)

    /**
     * 保存子元素y坐标
     */
    private val ys = IntArray(7)
    /**
     * 最小滑动阀值
     */
    var POIN_X = DisplayUtil.getScreenWidth() / 2
    var POIN_Y = DisplayUtil.getScreenHeight() / 2
    private val mLocationsss = arrayOf(intArrayOf(POIN_X - resources.getDimensionPixelSize(R.dimen.sm_px_20), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) - resources.getDimensionPixelSize(R.dimen.sm_px_190)), intArrayOf(POIN_X + resources.getDimensionPixelSize(R.dimen.sm_px_90), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) - resources.getDimensionPixelSize(R.dimen.sm_px_130)), intArrayOf(POIN_X + resources.getDimensionPixelSize(R.dimen.sm_px_70), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) - resources.getDimensionPixelSize(R.dimen.sm_px_40)), intArrayOf(POIN_X, DisplayUtil.getScreenHeight() / 2 - resources.getDimensionPixelSize(R.dimen.sm_px_40)),
            intArrayOf(POIN_X - resources.getDimensionPixelSize(R.dimen.sm_px_20), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) + resources.getDimensionPixelSize(R.dimen.sm_px_70)), intArrayOf(POIN_X - resources.getDimensionPixelSize(R.dimen.sm_px_40), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) + resources.getDimensionPixelSize(R.dimen.sm_px_140)), intArrayOf(POIN_X - resources.getDimensionPixelSize(R.dimen.sm_px_70), POIN_Y - resources.getDimensionPixelSize(R.dimen.sm_px_30) + resources.getDimensionPixelSize(R.dimen.sm_px_250)))
    private var mMatrix: Matrix? = null
    private var mPathMeasure: PathMeasure? = null
    private var mPos: FloatArray? = null
    private var mTan: FloatArray? = null
    private var mDistance: Float = 0.toFloat()
    private var mChildWidth: Int = 0
    private var mChildHeight: Int = 0
    private var mSlideRatio: Float = 0.toFloat()
    private var mChildCount: Int = 0
    private var mCurrentSlideDirection: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()

    }

    private fun init() {
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        path = Path()
        mPathMeasure = PathMeasure(path, false)
        mMatrix = Matrix()
        mPos = FloatArray(2)
        mTan = FloatArray(2)
        mDistance = 0f

        paint = Paint()
        paint!!.color = Color.YELLOW
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 3f
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        initializeChildViews()

    }

    /**
     * 初始化子元素信息(坐标位置，缩放参数)
     */
    private fun initializeChildViews() {
        mChildCount = childCount
        var i = 0
        val size = childCount
        while (i < size) {
            val child = getChildAt(i)
            mChildWidth = child.measuredWidth
            mChildHeight = child.measuredHeight
            val index = mLocationsss[i]
            child.layout(index[0] - mChildWidth / 2, index[1] - mChildHeight / 2, index[0] + mChildWidth / 2, index[1] + mChildHeight / 2)
            xs[i] = mLocationsss[i][0]
            ys[i] = mLocationsss[i][1]

            val coordinate = Coordinate()
            coordinate.index = i

            coordinate.currentX = mLocationsss[i][0].toFloat()
            coordinate.currentY = mLocationsss[i][1].toFloat()

            if (i == 0) {
                coordinate.previousX = mLocationsss[6][0].toFloat()
                coordinate.previousY = mLocationsss[6][1].toFloat()
            } else {
                coordinate.previousX = mLocationsss[i - 1][0].toFloat()
                coordinate.previousY = mLocationsss[i - 1][1].toFloat()
            }

            if (i < size - 1) {
                coordinate.nextX = mLocationsss[i + 1][0].toFloat()
                coordinate.nextY = mLocationsss[i + 1][1].toFloat()
            }

            if (i == size - 1) {
                coordinate.nextX = mLocationsss[0][0].toFloat()
                coordinate.nextY = mLocationsss[0][1].toFloat()
            }

            child.tag = coordinate
            i++
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val childCount = childCount
        Log.e("childCount", childCount.toString() + "")
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = event.x
                 yDown = event.y
            }
            MotionEvent.ACTION_UP ->
                //手指抬起时垂直滑动距离大于设置的最小滑动距离
                if (Math.abs(event.y - yDown) > MIN_SLIDE_DISTANCE) {

                    //滑动比例大于等于1，更新子元素坐标，比例信息
                    if (Math.abs(mSlideRatio) >= 1) {

                        /***************再次修正防止星球未滑动到指定位置start */
                        moveNext(0, 1.0)
                        moveNext(1, 1.0)
                        moveNext(2, 1.0)
                        moveNext(3, 1.0)
                        moveNext(4, 1.0)
                        moveNext(5, 1.0)
                        moveNext(6, 1.0)
                        /***************再次修正防止星球未滑动到指定位置 end */

                        updateChildInfo()
                    } //滑动比例小于1时抬起手指，自动修正滑动位置
                    else {
                        amendmentDistance()
                    }


                }
            MotionEvent.ACTION_MOVE -> {
                mSlideRatio = (event.y - yDown) / (DisplayUtil.getScreenHeight() / 2)
                mCurrentSlideDirection = if (event.y - yDown > 0) SLIDE_DIRECTIONDOWN else SLIDE_DIRECTION_TOP
                val bigDecimal = BigDecimal(mSlideRatio.toDouble())
                var result = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                if (Math.abs(result) <= 1 && result != 0.0 && Math.abs(event.y - yDown) > MIN_SLIDE_DISTANCE) {
                    //修正一波移动比例计算结果，防止移动比例出现0.96/0.97等情况
                    if (result >= 1 - 0.03) {
                        result = 1.0
                    }

                    if (result <= 0.03 - 1) {
                        result = -1.0
                    }

                    //下滑，控制每个子元素往下一目标点滑动
                    if (mCurrentSlideDirection == SLIDE_DIRECTIONDOWN) {
                        moveNext(0, result)
                        moveNext(1, result)
                        moveNext(2, result)
                        moveNext(3, result)
                        moveNext(4, result)
                        moveNext(5, result)
                        moveNext(6, result)
                    } else {
                        movePrevious(0, result)
                        movePrevious(1, result)
                        movePrevious(2, result)
                        movePrevious(3, result)
                        movePrevious(4, result)
                        movePrevious(5, result)
                        movePrevious(6, result)
                    }//上滑，控制每个子元素往上一目标点滑动
                }
            }
        }
        //记录当前触摸X Y坐标
        mposX = xDown
        mposY = yDown
        return true
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        paint!!.color = Color.YELLOW
        canvas.drawCircle(200f,200f,63f,paint)
       var  mPaint=Paint()
        mPaint.isAntiAlias=true
        mPaint.color=Color.WHITE
        mPaint.style=Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth=5f
        canvas.drawCircle(200f,200f,60f,mPaint)

        for (i in mLocationsss.indices) {
            val index = mLocationsss[i]
            canvas.drawCircle(index[0].toFloat(), index[1].toFloat(), 4f, paint!!)
            //            canvas.drawBitmap(mBitmap, index[0] - mBitmap.getWidth() / 2-DX, index[1] - mBitmap.getHeight() / 2-DX, paint);
        }
        val pts = floatArrayOf(mLocationsss[0][0].toFloat(), mLocationsss[0][1].toFloat(), mLocationsss[1][0].toFloat(), mLocationsss[1][1].toFloat(), mLocationsss[1][0].toFloat(), mLocationsss[1][1].toFloat(), mLocationsss[2][0].toFloat(), mLocationsss[2][1].toFloat(), mLocationsss[2][0].toFloat(), mLocationsss[2][1].toFloat(), mLocationsss[3][0].toFloat(), mLocationsss[3][1].toFloat(), mLocationsss[3][0].toFloat(), mLocationsss[3][1].toFloat(), mLocationsss[4][0].toFloat(), mLocationsss[4][1].toFloat(), mLocationsss[4][0].toFloat(), mLocationsss[4][1].toFloat(), mLocationsss[5][0].toFloat(), mLocationsss[5][1].toFloat(), mLocationsss[5][0].toFloat(), mLocationsss[5][1].toFloat(), mLocationsss[6][0].toFloat(), mLocationsss[6][1].toFloat(), mLocationsss[6][0].toFloat(), mLocationsss[6][1].toFloat(), mLocationsss[0][0].toFloat(), mLocationsss[0][1].toFloat())
        canvas.drawLines(pts, paint!!)
    }


    /**
     * 子元素往下一个坐标移动处理
     *
     * @param childIndex 待移动子元素索引
     * @param rate       移动比例
     */
    private fun moveNext(childIndex: Int, rate: Double) {
        val child = getChildAt(childIndex)
        if (child.tag == null)
            return
        val coordinate = child.tag as Coordinate
        child.x = (coordinate.currentX - mChildWidth / 2 + (coordinate.nextX - coordinate.currentX) * rate).toFloat()
        child.y = (coordinate.currentY - mChildHeight / 2 + (coordinate.nextY - coordinate.currentY) * rate).toFloat()
    }

    /**
     * 子元素往上一个坐标移动处理
     *
     * @param childIndex 待移动子元素索引
     * @param rate       移动比例
     */
    private fun movePrevious(childIndex: Int, rate: Double) {
        val child = getChildAt(childIndex)
        if (child.tag == null)
            return
        val coordinate = child.tag as Coordinate
        child.x = (coordinate.currentX - mChildWidth / 2 + (coordinate.currentX - coordinate.previousX) * rate).toFloat()
        child.y = (coordinate.currentY - mChildHeight / 2 + (coordinate.currentY - coordinate.previousY) * rate).toFloat()
    }


    /**
     * 每次滑动完全时更新子元素坐标，缩放比例，所在位置信息
     */
    private fun updateChildInfo() {
        for (i in 0 until mChildCount) {
            val view = getChildAt(i)
            val coordinate = view.tag as Coordinate ?: continue
            if (mCurrentSlideDirection == SLIDE_DIRECTIONDOWN) {
                if (coordinate.index == mChildCount - 1) {
                    coordinate.index = 0
                } else {
                    coordinate.index += 1
                }

            } else if (mCurrentSlideDirection == SLIDE_DIRECTION_TOP) {
                if (coordinate.index == 0) {
                    coordinate.index = mChildCount - 1
                } else {
                    coordinate.index -= 1
                }
            }
            coordinate.currentX = xs[coordinate.index].toFloat()
            coordinate.currentY = ys[coordinate.index].toFloat()

            coordinate.previousX = xs[if (coordinate.index == 0) mChildCount - 1 else coordinate.index - 1].toFloat()
            coordinate.previousY = ys[if (coordinate.index == 0) mChildCount - 1 else coordinate.index - 1].toFloat()
            coordinate.nextX = xs[if (coordinate.index == mChildCount - 1) 0 else coordinate.index + 1].toFloat()
            coordinate.nextY = ys[if (coordinate.index == mChildCount - 1) 0 else coordinate.index + 1].toFloat()

            view.tag = coordinate
        }
    }

    /**
     * 当用户手指滑动比例不够时,自动修正距离
     */
    private fun amendmentDistance() {
        var i = 0
        val size = childCount
        while (i < size) {
            val view = getChildAt(i)
            val coordinate = view.tag as Coordinate
            if (coordinate == null) {
                i++
                continue
            }

            //滑动比例少于40%手指抬起后回到原位置
            if (Math.abs(mSlideRatio) < AUTO_SLIDE_PERCENT) {
                animationSlide(view, coordinate.currentX - mChildWidth / 2, coordinate.currentY - mChildHeight / 2)
            } else if (Math.abs(mSlideRatio) > AUTO_SLIDE_PERCENT) {
                if (mCurrentSlideDirection == SLIDE_DIRECTIONDOWN) {
                    if (coordinate.index == mChildCount - 1) {
                        view.x = coordinate.nextX - mChildWidth / 2
                        view.y = coordinate.nextY - mChildHeight / 2
                    } else {
                        animationSlide(view, coordinate.nextX - mChildWidth / 2, coordinate.nextY - mChildHeight / 2)
                    }
                } else if (mCurrentSlideDirection == SLIDE_DIRECTION_TOP) {
                    if (coordinate.index == 0) {
                        view.x = coordinate.previousX - mChildWidth / 2
                        view.y = coordinate.previousY - mChildHeight / 2
                    } else {
                        animationSlide(view, coordinate.previousX - mChildWidth / 2, coordinate.previousY - mChildHeight / 2)
                    }
                }
            }//大于40%时，滑动到下一个点
            i++
        }

        if (Math.abs(mSlideRatio) > AUTO_SLIDE_PERCENT) {
            updateChildInfo()
        }
    }

    /**
     * 开启动画修正每个点的最终位置
     *
     * @param changedView
     * @param targetX
     * @param targetY
     */
    private fun animationSlide(changedView: View, targetX: Float, targetY: Float) {
        val fromX = changedView.x
        val fromY = changedView.y

        val translateAnimation = TranslateAnimation(0f, targetX - fromX, 0f, targetY - fromY)
        translateAnimation.duration = 250
        translateAnimation.fillAfter = true
        changedView.startAnimation(translateAnimation)
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                changedView.clearAnimation()
                changedView.x = targetX
                changedView.y = targetY
                translateAnimation.cancel()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * 保存子元素的当前位置、坐标、缩放比例信息
     */
    private inner class Coordinate {

        /**
         * 当前所有位置
         */
        var index: Int = 0

        /**
         * 上一个x坐标
         */
        var previousX: Float = 0.toFloat()

        /**
         * 上一个y坐标
         */
        var previousY: Float = 0.toFloat()

        /**
         * 上一个点缩放比例
         */
        private val previousScale: Float = 0.toFloat()

        /**
         * 当前x坐标
         */
        var currentX: Float = 0.toFloat()

        /**
         * 当前y坐标
         */
        var currentY: Float = 0.toFloat()

        /**
         * 当前缩放比例
         */
        var currentScale: Float = 0.toFloat()

        /**
         * 下一个x坐标
         */
        var nextX: Float = 0.toFloat()

        /**
         * 下一个y坐标
         */
        var nextY: Float = 0.toFloat()

        /**
         * 下一个点缩放比例
         */
        var nextScale: Float = 0.toFloat()
    }

    companion object {
        /**
         * 六个球所在的x,y坐标
         */
        /**
         * 手指上滑操作
         */
        private val SLIDE_DIRECTION_TOP = 0
        /**
         * 自动滑动比例
         */
        private val AUTO_SLIDE_PERCENT = 0.4f
        /**
         * 手指下滑操作
         */
        private val SLIDE_DIRECTIONDOWN = 1
        private val MIN_SLIDE_DISTANCE = 10
    }
}
