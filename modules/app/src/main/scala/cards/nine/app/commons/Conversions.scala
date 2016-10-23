package cards.nine.app.commons

import android.content.Intent
import cards.nine.app.ui.commons.Constants._
import cards.nine.models.types._
import cards.nine.models.{NotCategorizedPackage, SharedCollection, SharedCollectionPackage, _}
import cards.nine.process.cloud.models._

import scala.util.Random

trait Conversions
  extends AppNineCardsIntentConversions {

  def toSeqFormedCollection(collections: Seq[CloudStorageCollection]): Seq[FormedCollection] = collections map toFormedCollection

  def toFormedCollection(userCollection: CloudStorageCollection): FormedCollection = FormedCollection(
    name = userCollection.name,
    originalSharedCollectionId = userCollection.originalSharedCollectionId,
    sharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
    items = userCollection.items map toFormedItem,
    collectionType = userCollection.collectionType,
    icon = userCollection.icon,
    category = userCollection.category,
    moment = userCollection.moment map toMoment)

  def toFormedItem(item: CloudStorageCollectionItem): FormedItem = FormedItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.intent)

  def toCardData(contact: Contact): CardData =
    CardData(
      term = contact.name,
      packageName = None,
      cardType = ContactCardType,
      intent = contactToNineCardIntent(contact.lookupKey),
      imagePath = Option(contact.photoUri))

  def toCollectionDataFromSharedCollection(collection: SharedCollection, cards: Seq[CardData]): CollectionData =
    CollectionData(
      name = collection.name,
      collectionType = AppsCollectionType,
      icon = collection.icon,
      themedColorIndex = Random.nextInt(numSpaces),
      appsCategory = Option(collection.category),
      cards = cards,
      originalSharedCollectionId = Option(collection.sharedCollectionId),
      sharedCollectionId = Option(collection.sharedCollectionId),
      publicCollectionStatus = collection.publicCollectionStatus)

  def toCardData(app: SharedCollectionPackage): CardData =
    CardData(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = NoInstalledAppCardType,
      intent = toNineCardIntent(app))

  def toCardData(app: ApplicationData): CardData =
    CardData(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app))

  def toCardData(app: NotCategorizedPackage): CardData =
    CardData(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app))

  def toDockAppData(cloudStorageDockApp: CloudStorageDockApp): DockAppData =
    DockAppData(
      name = cloudStorageDockApp.name,
      dockType = cloudStorageDockApp.dockType,
      intent = jsonToNineCardIntent(cloudStorageDockApp.intent),
      imagePath = cloudStorageDockApp.imagePath,
      position = cloudStorageDockApp.position)

}

trait AppNineCardsIntentConversions extends NineCardsIntentConversions {

  def toNineCardIntent(app: SharedCollectionPackage): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(NineCardsIntentExtras.openNoInstalledApp)
    intent
  }

  def toNineCardIntent(app: NotCategorizedPackage): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(NineCardsIntentExtras.openNoInstalledApp)
    intent
  }

  def phoneToNineCardIntent(lookupKey: Option[String], tel: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      contact_lookup_key = lookupKey,
      tel = Option(tel)))
    intent.setAction(NineCardsIntentExtras.openPhone)
    intent
  }

  def smsToNineCardIntent(lookupKey: Option[String], tel: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      contact_lookup_key = lookupKey,
      tel = Option(tel)))
    intent.setAction(NineCardsIntentExtras.openSms)
    intent
  }

  def emailToNineCardIntent(lookupKey: Option[String], email: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      contact_lookup_key = lookupKey,
      email = Option(email)))
    intent.setAction(NineCardsIntentExtras.openEmail)
    intent
  }

  def contactToNineCardIntent(lookupKey: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      contact_lookup_key = Option(lookupKey)))
    intent.setAction(NineCardsIntentExtras.openContact)
    intent
  }

  def toNineCardIntent(intent: Intent): NineCardsIntent = {
    val i = NineCardsIntent(NineCardsIntentExtras())
    i.fill(intent)
    i
  }

  def toNineCardIntent(packageName: String, className: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(packageName),
      class_name = Option(className)))
    intent.setAction(NineCardsIntentExtras.openApp)
    intent.setClassName(packageName, className)
    intent
  }

  def toMoment(cloudStorageMoment: CloudStorageMoment): FormedMoment =
    FormedMoment(
      collectionId = None,
      timeslot = cloudStorageMoment.timeslot map toTimeSlot,
      wifi = cloudStorageMoment.wifi,
      headphone = cloudStorageMoment.headphones,
      momentType = cloudStorageMoment.momentType,
      widgets = cloudStorageMoment.widgets map toWidgetDataSeq)

  def toMomentData(cloudStorageMoment: CloudStorageMoment): MomentData =
    MomentData(
      collectionId = None,
      timeslot = cloudStorageMoment.timeslot map toTimeSlot,
      wifi = cloudStorageMoment.wifi,
      headphone = cloudStorageMoment.headphones,
      momentType = cloudStorageMoment.momentType,
      widgets = cloudStorageMoment.widgets map toWidgetDataSeq)

  def toWidgetDataSeq(widgets: Seq[CloudStorageWidget]) =
    widgets map toWidgetData

  def toWidgetData(widget: CloudStorageWidget): WidgetData =
    WidgetData(
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = None,
      area = WidgetArea(
        startX = widget.area.startX,
        startY = widget.area.startY,
        spanX = widget.area.spanX,
        spanY = widget.area.spanY),
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent map jsonToNineCardIntent)

  def toTimeSlot(cloudStorageMomentTimeSlot: CloudStorageMomentTimeSlot): MomentTimeSlot =
    MomentTimeSlot(
      from = cloudStorageMomentTimeSlot.from,
      to = cloudStorageMomentTimeSlot.to,
      days = cloudStorageMomentTimeSlot.days)

}