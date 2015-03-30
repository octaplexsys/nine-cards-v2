package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.res.ColorStateList
import android.graphics.{Color, Paint}
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable._
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.widget._
import com.fortysevendeg.macroid.extras.GridLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.modules.repository.Collection
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.ui.components.Dimen
import com.fortysevendeg.ninecardslauncher.ui.launcher.CollectionItemTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Ui, ActivityContext, AppContext, Tweak}
import com.fortysevendeg.ninecardslauncher.utils.ColorsUtils._

class LauncherWorkSpaceCollectionsHolder(parentDimen: Dimen)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder
  with CollectionsGroupStyle {

  var grid = slot[GridLayout]

  val views = 0 to (numSpaces - 1) map (new CollectionItem(_))

  addView(getUi(l[GridLayout]() <~ wire(grid) <~ collectionGridStyle))

  runUi(grid <~ glAddViews(
    views = views,
    columns = numInLine,
    rows = numInLine,
    width = parentDimen.width / numInLine,
    height = parentDimen.height / numInLine))

  def populate(data: LauncherData): Ui[_] = {
    val uiSeq = for {
      row <- 0 until numInLine
      column <- 0 until numInLine
    } yield {
        val position = (row * numInLine) + column
        val view = grid map (_.getChildAt(position).asInstanceOf[CollectionItem])
        // TODO we should use a sequance UI
        data.collections.lift(position) map {
          collection =>
            view <~ vVisible <~ ciPopulate(collection)
        } getOrElse view <~ vGone
      }
    Ui.sequence(uiSeq: _*)
  }

}

class CollectionItem(position: Int)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends FrameLayout(activityContext.get)
  with CollectionItemStyle {

  var collection: Option[Collection] = None

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var icon = slot[ImageView]

  var name = slot[TextView]

  setTag(position)

  addView(
    getUi(
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ collectionItemStyle <~ On.click {
        collection map (c => uiShortToast(c.name)) getOrElse Ui.nop
      } <~ vTag(R.id.use_layer_hardware, "")
    )
  )

  def populate(collection: Collection) = {
    this.collection = Some(collection)
    resGetDrawableIdentifier(collection.icon) map {
      resIcon =>
        runUi(
          (icon <~ ivSrc(resIcon) <~ vBackground(createBackground(collection.themedColorIndex))) ~
            (name <~ tvText(collection.name))
        )
    }
  }

  private def createBackground(indexColor: Int): Drawable = {
    val resColor = indexColor match {
      case 0 => R.color.collection_group_1
      case 1 => R.color.collection_group_2
      case 2 => R.color.collection_group_3
      case 3 => R.color.collection_group_4
      case 4 => R.color.collection_group_5
      case 5 => R.color.collection_group_6
      case 6 => R.color.collection_group_7
      case 7 => R.color.collection_group_8
      case _ => R.color.collection_group_9
    }

    val color = resGetColor(resColor)

    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(getColorDark(color, 0.2f))),
        getDrawable(color),
        null)
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), getDrawable(getColorDark(color)))
      states.addState(Array.emptyIntArray, getDrawable(color))
      states
    }
  }

  private def getDrawable(color: Int): Drawable = {
    val drawableColor = createShapeDrawable(color)
    Lollipop ifSupportedThen {
      drawableColor
    } getOrElse {
      val padding = resGetDimensionPixelSize(R.dimen.elevation_default)
      val drawableShadow = createShapeDrawable(resGetColor(R.color.shadow_default))
      val layer = new LayerDrawable(Array(drawableShadow, drawableColor))
      layer.setLayerInset(0, padding, padding, padding, 0)
      layer.setLayerInset(1, padding, 0, padding, padding)
      layer
    }
  }

  private def createShapeDrawable(color: Int) = {
    val drawableColor = new ShapeDrawable(new OvalShape())
    drawableColor.getPaint.setColor(color)
    drawableColor.getPaint.setStyle(Paint.Style.FILL)
    drawableColor.getPaint.setAntiAlias(true)
    drawableColor
  }

}

object CollectionItemTweaks {
  type W = CollectionItem

  def ciPopulate(collection: Collection) = Tweak[W](_.populate(collection))
}