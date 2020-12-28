package com.start

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.night.DisplayUtil
import java.util.*
import kotlin.math.max

class StarGroupLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val animalList = arrayListOf<ValueAnimator>()

    init {
        setWillNotDraw(false)
    }

    fun upRoundView(childList: List<View>) {
        var pointList = arrayListOf<Point>()
        val widthM = measuredWidth / 2
        val heightM = measuredHeight / 2
        childList.forEachIndexed { index, view ->
            addView(view, FrameLayout.LayoutParams(-2, -2))
            when (index) {
                //右上
                0 -> {
                    pointList.add(
                            Point(
                                    widthM + DisplayUtil.dp2px(90f),
                                    heightM - DisplayUtil.dp2px(120f)
                            )
                    )
                }
                //右下
                1 -> {
                    pointList.add(
                            Point(
                                    widthM + DisplayUtil.dp2px(100f),
                                    heightM + DisplayUtil.dp2px(100f)
                            )
                    )
                }
                //左上
                2 -> {
                    pointList.add(
                            Point(
                                    widthM - DisplayUtil.dp2px(120f),
                                    heightM - DisplayUtil.dp2px(100f)
                            )
                    )
                }
            }
        }
        val length = DisplayUtil.dp2px(50f).toFloat()
        val valueAnimator = ValueAnimator.ofFloat(0f, length)
        valueAnimator.duration = 2000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.addUpdateListener { animation -> //获取当前位置
            val value = animation.animatedValue as Float
            childList.forEachIndexed { index, child ->
                child.x = pointList[index].x.toFloat()
                if (index == 1) {
                    child.y = pointList[index].y.toFloat() + value
                } else {
                    child.y = pointList[index].y.toFloat() - value
                }
            }
        }
        valueAnimator.start()
        animalList.add(valueAnimator)
    }

    fun upView(
            baseView: View,
            childList: List<View>,
            flattenScale: Float,
            x: Float,
            y: Float
    ) {
        childList.forEach {
            addView(it, FrameLayout.LayoutParams(-2, -2))
        }

        baseView.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (baseView == null) return
                        baseView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        var paint: Paint = Paint()
                        val point = intArrayOf(0, 0)
                        val wh = floatArrayOf(0f, 0f)
                        val mCurrentPosition = floatArrayOf(0f, 0f)
                        paint.isAntiAlias = true
                        baseView.getLocationInWindow(point)
                        wh[0] = baseView.width.toFloat() + DisplayUtil.dp2px(13f).toFloat()
                        wh[1] = baseView.height.toFloat()
                        //绘制连线
                        paint.strokeWidth = 3f
                        paint.style = Paint.Style.STROKE
                        val path = Path()
                        val mRectF = RectF(
                                point[0].toFloat(), point[1] - wh[1] / flattenScale,
                                point[0].toFloat() + wh[0], point[1] + wh[1] / flattenScale
                        )
                        path.addArc(mRectF, 0f, 360f)
                        val matrix = Matrix()
                        matrix.setRotate(
                                (-20).toFloat(),
                                point[0].toFloat(),
                                point[1].toFloat() - wh[1] / flattenScale
                        )
                        path.transform(matrix)
                        matrix.reset()
                        matrix.setTranslate(
                                DisplayUtil.dp2px(x).toFloat(),
                                DisplayUtil.dp2px(y).toFloat()
                        )
                        path.transform(matrix)
                        val pathMeasure = PathMeasure(path, true)
                        val length = pathMeasure.length
                        var interval = 200 + Random().nextInt(500)
                        Log.e("tagg", "${length}....${interval}")
                        val valueAnimator = ValueAnimator.ofFloat(0f, length)
                        valueAnimator.duration = 10000
                        valueAnimator.interpolator = LinearInterpolator()
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.addUpdateListener { animation -> //获取当前位置
                            val currValue = animation.animatedValue as Float
                            childList.forEachIndexed { index, child ->
                                val value = (currValue + interval * index) % length
                                pathMeasure.getPosTan(value, mCurrentPosition, null)
                                var scale = value / length
                                scale = when {
                                    scale < 0.25 -> {
                                        (scale + 0.25f) * 2
                                    }
                                    scale <= 0.75 -> {
                                        1 - (scale - 0.25f) * 2
                                    }
                                    else -> {
                                        (scale - 0.75f) * 2
                                    }
                                }
                                scale = max(0.1f, scale)
                                child.x = mCurrentPosition[0] - child.width / 2
                                child.y = mCurrentPosition[1] - child.height / 2
                                child.scaleX = scale
                                child.scaleY = scale
                                child.alpha = scale
                            }
                        }
                        valueAnimator.start()
                        animalList.add(valueAnimator)
                    }
                })

    }

    fun startAnimal() {
        animalList.forEach {
            if (it.isPaused)
                it.start()
        }
    }

    fun stopAnimal() {
        animalList.forEach {
            if (it.isRunning)
                it.pause()
        }
    }
}