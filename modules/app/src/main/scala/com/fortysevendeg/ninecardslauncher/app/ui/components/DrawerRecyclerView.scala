package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.{MotionEvent, ViewConfiguration}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.ScrollableManager
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

class DrawerRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var animatedController: Option[SearchBoxAnimatedController] = None

  val indicator = DrawerRecyclerIndicator()

  val touchSlop = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
  }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = if (indicator.disableScroll) {
    true
  } else {
    super.dispatchTouchEvent(ev)
  }

  addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    override def onTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Unit = {
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      animatedController foreach (_.initVelocityTracker(event))
      (MotionEventCompat.getActionMasked(event), indicator.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          val delta = indicator.deltaX(x)
          indicator.lastMotionX = x
          indicator.lastMotionY = y
          animatedController foreach { controller =>
            runUi(controller.movementByOverScroll(delta))
          }
        case (ACTION_MOVE, Stopped) =>
          setStateIfNeeded(x, y)
        case (ACTION_DOWN, _) =>
          indicator.lastMotionX = x
          indicator.lastMotionY = y
        case (ACTION_CANCEL | ACTION_UP, _) =>
          animatedController foreach (_.computeFling())
          indicator.touchState = Stopped
          blockScroll(false)
        case _ =>
      }
    }

    override def onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean = {
      animatedController foreach (_.initVelocityTracker(event))
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      (MotionEventCompat.getActionMasked(event), indicator.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          true
        case (ACTION_MOVE, _) =>
          setStateIfNeeded(x, y)
          indicator.touchState != Stopped
        case (ACTION_DOWN, _) =>
          indicator.lastMotionX = x
          indicator.lastMotionY = y
          false
        case (ACTION_CANCEL | ACTION_UP, _) =>
          animatedController foreach (_.computeFling())
          indicator.touchState = Stopped
          blockScroll(false)
          indicator.touchState != Stopped
        case _ => indicator.touchState != Stopped
      }
    }

    override def onRequestDisallowInterceptTouchEvent(b: Boolean): Unit = {

    }
  })

  private[this] def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - indicator.lastMotionX)
    val yDiff = math.abs(y - indicator.lastMotionY)

    val xMoved = xDiff > touchSlop

    if (xMoved) {
      val isScrolling = xDiff > yDiff
      if (isScrolling) {
        animatedController foreach (controller => runUi(controller.startMovement))
        indicator.touchState = Scrolling
        blockScroll(true)
      }
      indicator.lastMotionX = x
      indicator.lastMotionY = y
    }
  }

  private[this] def blockScroll(bs: Boolean) = getLayoutManager match {
    case lm: ScrollableManager => lm.blockScroll = bs
    case _ =>
  }

}

object DrawerRecyclerViewTweaks {
  type W = DrawerRecyclerView

  def drvDisableScroll(disable: Boolean) = Tweak[W](_.indicator.disableScroll = disable)

  def drvAddController(controller: SearchBoxAnimatedController) = Tweak[W](_.animatedController = Some(controller))

}

case class DrawerRecyclerIndicator(
  var disableScroll: Boolean = false,
  var lastMotionX: Float = 0,
  var lastMotionY: Float = 0,
  var touchState: ViewState = Stopped) {

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

}
