package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io._
import java.net.URL

import android.content.res.Resources
import android.graphics._
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.{DisplayMetrics, TypedValue}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef
import com.fortysevendeg.ninecardslauncher.services.image.{BitmapTransformationException, ImageServicesConfig}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.{Answer, Result}

import scalaz.Scalaz._
import scalaz.concurrent.Task

trait ImageServicesTasks {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  implicit def uncaughtConverter = (t : Throwable) => BitmapTransformationException(t.getMessage, t.some)

  def getPathByName(name: String): ServiceDef[ContextSupport, File, IOException] = Service { implicit context: ContextSupport =>
    Task {
      Result.catching[IOException] {
        new File(resourceUtils.getPath(if (name.isEmpty) "_" else name.substring(0, 1).toUpperCase))
      }
    }
  }

  def getPathByApp(packageName: String, className: String): ServiceDef[ContextSupport, File, IOException] = Service { implicit context: ContextSupport =>
    Task {
      Result.catching[IOException] {
        new File(resourceUtils.getPathPackage(packageName, className))
      }
    }
  }

  def getPathByPackageName(packageName: String): ServiceDef[ContextSupport, File, IOException] = Service { implicit context: ContextSupport =>
    Task {
      Result.catching[IOException] {
        new File(resourceUtils.getPath(packageName))
      }
    }
  }

  def getBitmapByApp(packageName: String, icon: Int): ServiceDef[ContextSupport, Bitmap, BitmapTransformationException] = Service { context: ContextSupport =>
    Task {
      val packageManager = context.getPackageManager
      Option(packageManager.getResourcesForApplication(packageName)) match {
        case Some(resources) =>
          val density = betterDensityForResource(resources, icon)
          tryIconByDensity(resources, icon, density) match {
            case answer@Answer(_) => answer
            case _ => tryIconByPackageName(packageName)
          }
        case _ => Result.errata(BitmapTransformationException("Resource not found from packageName"))
      }
    }
  }

  def getBitmapFromURL(uri: String): ServiceDef[ContextSupport, Bitmap, BitmapTransformationException] = Service { _ =>
    Task {
      CatchAll[BitmapTransformationException] {
        new URL(uri).getContent match {
          case is: InputStream => BitmapFactory.decodeStream(is)
          case _ => throw BitmapTransformationException(s"Unexpected error while fetching content from uri: $uri")
        }
      }
    }
  }

  def saveBitmap(file: File, bitmap: Bitmap): ServiceDef[ContextSupport, Unit, IOException] = Service { _ =>
    Task {
      Result.catching[IOException] {
        val out: FileOutputStream = new FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
      }
    }
  }

  def getBitmapByAppOrName(packageName: String, icon: Int, name: String):
  ServiceDef[ContextSupport, Bitmap, BitmapTransformationException] = Service { context: ContextSupport =>
    manageBitmapTask(name)(getBitmapByApp(packageName, icon).run(context))
  }

  def getBitmapFromURLOrName(url: String, name: String):
  ServiceDef[ContextSupport, Bitmap, BitmapTransformationException] = Service { context: ContextSupport =>
    manageBitmapTask(name)(getBitmapFromURL(url).run(context))(context, )
  }

  def getBitmapByName(text: String)(implicit context: ContextSupport, config: ImageServicesConfig): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
        val bitmap: Bitmap = Bitmap.createBitmap(defaultSize, defaultSize, Bitmap.Config.RGB_565)
        val bounds: Rect = new Rect
        val paint = defaultPaint
        paint.getTextBounds(text, 0, text.length, bounds)
        val x: Int = ((defaultSize / 2) - bounds.exactCenterX).toInt
        val y: Int = ((defaultSize / 2) - bounds.exactCenterY).toInt
        val canvas: Canvas = new Canvas(bitmap)
        val color = config.colors(scala.util.Random.nextInt(config.colors.length))
        canvas.drawColor(color)
        canvas.drawText(text, x, y, paint)
        bitmap
      }

  private[this] def manageBitmapTask(name: String)(getBitmap: => Task[Result[Bitmap, BitmapTransformationException]])
    (implicit context: ContextSupport, config: ImageServicesConfig) =
    getBitmap map {
      case answer @ Answer(_) => answer
      case _ => getBitmapByName(name)
    }

  private[this] def currentDensity(implicit context: ContextSupport): Int =
    context.getResources.getDisplayMetrics.densityDpi

  private[this] def densities(implicit context: ContextSupport): List[Int] = currentDensity match {
    case DisplayMetrics.DENSITY_HIGH =>
      List(DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
    case _ =>
      List(DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
  }

  private[this] def betterDensityForResource(resources: Resources, id: Int)(implicit context: ContextSupport): Int =
    densities.find(density => Result(resources.getValueForDensity(id, density, new TypedValue, true)).isAnswer) getOrElse noDensity

  private[this] def defaultSize(implicit context: ContextSupport): Int = {
    val metrics: DisplayMetrics = context.getResources.getDisplayMetrics
    val width: Int = metrics.widthPixels / 3
    val height: Int = metrics.heightPixels / 3
    if (width > height) width else height
  }

  private[this] def defaultPaint(implicit context: ContextSupport): Paint = {
    def determineMaxTextSize(maxWidth: Float): Int = {
      var size: Int = 0
      val paint: Paint = new Paint
      do {
        size = size + 1
        paint.setTextSize(size)
      } while (paint.measureText("M") < maxWidth)
      size
    }
    val paint = new Paint
    paint.setColor(Color.WHITE)
    paint.setStyle(Paint.Style.FILL)
    paint.setAntiAlias(true)
    paint.setTextSize(determineMaxTextSize((defaultSize / 3) * 2))
    paint
  }

  private[this] def tryIconByDensity(resources: Resources, icon: Int, density: Int): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
      val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) resources.getDrawableForDensity(icon, density, null)
      else resources.getDrawableForDensity(icon, density)
      d.asInstanceOf[BitmapDrawable].getBitmap
    }

  private[this] def tryIconByPackageName(packageName: String)(implicit context: ContextSupport): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
      context.getPackageManager.getApplicationIcon(packageName).asInstanceOf[BitmapDrawable].getBitmap
    }

}

object ImageServicesTasks extends ImageServicesTasks