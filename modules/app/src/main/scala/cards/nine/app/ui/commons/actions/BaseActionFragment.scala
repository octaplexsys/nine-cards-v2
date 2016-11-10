package cards.nine.app.ui.commons.actions

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import ActionsSnails._
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models._
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerTextColor, PrimaryColor}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ProgressBarTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.language.postfixOps

trait BaseActionFragment
  extends BottomSheetDialogFragment
  with TypedFindView
  with ContextSupportProvider
  with UiExtensions
  with Contexts[Fragment] {

  val defaultValue = 0

  implicit lazy val di: Injector = new InjectorImpl

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  implicit lazy val theme: NineCardsTheme =
    di.themeProcess.getTheme(Theme.getThemeFile).resolveNow match {
      case Right(t) => t
      case _ => getDefaultTheme
    }

  private[this] lazy val defaultColor = theme.get(PrimaryColor)

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  protected var width: Int = 0

  protected var height: Int = 0

  protected lazy val colorPrimary = getInt(Seq(getArguments), BaseActionFragment.colorPrimary, defaultColor)

  protected lazy val backgroundColor = theme.get(DrawerBackgroundColor)

  protected lazy val toolbar = Option(findView(TR.actions_toolbar))

  protected lazy val loading = Option(findView(TR.action_loading))

  protected lazy val content = Option(findView(TR.action_content_layout))

  protected lazy val rootContent = Option(findView(TR.action_content_root))

  protected lazy val fab = Option(findView(TR.action_content_fab))

  protected lazy val errorContent = Option(findView(TR.actions_content_error_layout))

  protected lazy val errorMessage = Option(findView(TR.actions_content_error_message))

  protected lazy val errorIcon = Option(findView(TR.actions_content_error_icon))

  protected lazy val errorButton = Option(findView(TR.actions_content_error_button))

  protected var rootView: Option[FrameLayout] = None

  def getLayoutId: Int

  def useFab: Boolean = false

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)

    val baseView = LayoutInflater.from(getActivity).inflate(R.layout.base_action_fragment, javaNull, false).asInstanceOf[FrameLayout]
    val layout = LayoutInflater.from(getActivity).inflate(getLayoutId, javaNull)
    rootView = Option(baseView)
    ((rootView <~ vBackgroundColor(backgroundColor)) ~
      (errorContent <~ vBackgroundColor(backgroundColor)) ~
      (content <~ vgAddView(layout))  ~
      (loading <~ pbColor(colorPrimary)) ~
      (errorIcon <~ tivColor(colorPrimary)) ~
      (rootContent <~ vInvisible) ~
      (errorContent <~ vGone) ~
      (errorMessage <~ tvColor(theme.get(DrawerTextColor).alpha(0.8f))) ~
      (errorButton <~ vBackgroundTint(colorPrimary)) ~
      (rootContent <~ showContent())).run
    dialog.setContentView(baseView)
  }
  def unreveal(): Ui[Any] = Ui(dismiss())

  def showMessageInScreen(message: Int, error: Boolean, action: => Unit): Ui[_] =
    (loading <~ vGone) ~
      (errorIcon <~ ivSrc(if (error) R.drawable.placeholder_error else R.drawable.placeholder_empty)) ~
      (errorMessage <~ text(message)) ~
      (errorButton <~ On.click {
        action
        hideError
      }) ~
      (errorContent <~ vVisible)

  def hideError: Ui[_] = errorContent <~ vGone

}

object BaseActionFragment {
  val packages = "packages"
  val colorPrimary = "color_primary"
}