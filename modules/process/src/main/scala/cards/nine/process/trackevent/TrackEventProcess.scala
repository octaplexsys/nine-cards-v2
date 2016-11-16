package cards.nine.process.trackevent

import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{NineCardsMoment, Category, MomentCategory}

trait TrackEventProcess {

  /* AppDrawerScreen */

  /**
    * Tracks when the user uses the fast scroller
    */
  def usingFastScroller(): TaskService[Unit]

  /**
    * Tracks when the user goes to contacts
    */
  def goToContacts(): TaskService[Unit]

  /**
    * Tracks when the user goes to apps
    */
  def goToApps(): TaskService[Unit]

  /**
    * Tracks when the user adds an app to a collection
    *
    * @param packageName the package's name
    */
  def addAppToCollection(packageName: String): TaskService[Unit]

  /**
    * Tracks when the user adds a contact to a collection
    */
  def addContactToCollection(): TaskService[Unit]

  /**
    * Tracks when the user the Google Play search button
    */
  def goToGooglePlayButton(): TaskService[Unit]

  /**
    * Tracks when the user the Google call button
    */
  def goToGoogleCallButton(): TaskService[Unit]

  /**
    * Tracks when the user filters the apps
    *
    * @param filterName the filter's name
    */
  def goToFiltersByButton(filterName: String): TaskService[Unit]

  /* CollectionDetailScreen */

  /**
    * Tracks when the user uses the navigation bar
    */
  def useNavigationBar(): TaskService[Unit]

  /**
    * Tracks when the user reorder an app in the collection
    *
    * @param newPosition the new app's position
    */
  def reorderApplication(newPosition: Int): TaskService[Unit]

  /**
    * Tracks when the user moves an app to another collection
    *
    * @param collectionName the new collection's name
    */
  def moveApplications(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user removes some apps
    *
    * @param packageNames the sequence with the removed apps' packageNames
    */
  def removeApplications(packageNames: Seq[String]): TaskService[Unit]

  /**
    * Tracks when the user removes some apps
    */
  def closeCollectionByGesture(): TaskService[Unit]

  /**
    * Tracks when the user removes some apps by FAB
    *
    * @param shortcutName the shortcut's name
    */
  def addShortcutByFab(shortcutName: String): TaskService[Unit]

  /**
    * Tracks when the user adds a recommended app by FAB
    *
    * @param packageName the package's name
    */
  def addRecommendationByFab(packageName: String): TaskService[Unit]

  /**
    * Tracks when the user adds a recommended app by FAB
    */
  def addContactByFab(): TaskService[Unit]

  /**
    * Tracks when the user add some apps by the FAB
    *
    * @param packageNames the sequence with the added apps' packageNames
    */
  def addAppsByFab(packageNames: Seq[String]): TaskService[Unit]

  /**
    * Tracks when the user removes some apps by the FAB
    *
    * @param packageNames the sequence with the removed apps' packageNames
    */
  def removeAppsByFab(packageNames: Seq[String]): TaskService[Unit]

  /**
    * Tracks when the user uses the adds a card menu option
    */
  def addCardByMenu(): TaskService[Unit]

  /**
    * Tracks when the user starts publishing a collection
    *
    * @param collectionName the collection's name
    */
  def publishCollectionByMenu(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user shares a just published a collection
    *
    * @param sharedCollectionId the sharedCollectionId
    */
  def shareCollectionAfterPublishing(sharedCollectionId: String): TaskService[Unit]

  /**
    * Tracks when the user shares an already published a collection
    *
    * @param sharedCollectionId the sharedCollectionId
    */
  def shareCollectionByMenu(sharedCollectionId: String): TaskService[Unit]

  /**
    * Tracks when the user opens an application from collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Tracks when the user adds an app to collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def addAppToCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Tracks when the user removes an application in collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def removeFromCollection(packageName: String, category: Category): TaskService[Unit]

  /* LauncherScreen */

  /**
    * Tracks when the user opens an application from app drawer
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromAppDrawer(packageName: String, category: Category): TaskService[Unit]

  /* ProfileScreen */

  /**
    * Tracks when the user enters in logs out
    *
    */
  def logout(): TaskService[Unit]

  /**
    * Tracks when the user enters in the Accounts tab in Profile
    *
    */
  def showAccountsContent(): TaskService[Unit]

  /**
    * Tracks when the user copies a configuration
    *
    */
  def copyConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user synchronizes the current configuration
    *
    */
  def synchronizeConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user synchronizes the configuration name
    *
    */
  def changeConfigurationName(): TaskService[Unit]

  /**
    * Tracks when the user deletes a configuration
    *
    */
  def deleteConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user enters in the Publications tab in Profile
    *
    */
  def showPublicationsContent(): TaskService[Unit]

  /**
    * Tracks when the user adds a collection from the Publications tab
    *
    * @param collectionName name of the collection
    */
  def addToMyCollectionsFromProfile(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user shares a collection from the Publications tab
    *
    * @param collectionName name of the collection
    */
  def shareCollectionFromProfile(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user enters in the Subscriptions tab in Profile
    *
    */
  def showSubscriptionsContent(): TaskService[Unit]

  /**
    * Tracks when the user subscribes to a collection in the Subscriptions tab
    *
    * @param sharedCollectionId of the collection
    */
  def subscribeToCollection(sharedCollectionId: String): TaskService[Unit]

  /**
    * Tracks when the user unsubscribes from a collection in the Subscriptions tab
    *
    * @param sharedCollectionId of the collection
    */
  def unsubscribeFromCollection(sharedCollectionId: String): TaskService[Unit]

  /* WidgetScreen */

  /**
    * Tracks when the user adds a widget in moment
    *
    * @param packageName package name of app
    * @param className class of the widget
    * @param moment moment where it's added
    */
  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory): TaskService[Unit]


  /* WizardScreen */

  /**
    * Tracks when the user choose an account in the Wizard's start screen
    *
    */
  def chooseAccount(): TaskService[Unit]

  /**
    * Tracks when the user choose a configuration in the Wizard's configuration screen
    *
    */
  def chooseNewConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user choose an existing device in the Wizard's configuration screen
    *
    */
  def chooseExistingDevice(): TaskService[Unit]

  /**
    * Tracks when the user choose a moment in the Wizard's moments screen
    *
    */
  def chooseMoment(moment: NineCardsMoment): TaskService[Unit]

  /**
    * Tracks when the user choose sets the wifi for a moment in the Wizard's moments screen
    *
    */
  def chooseMomentWifi(moment: NineCardsMoment): TaskService[Unit]

  /**
    * Tracks when the user choose other moment in the Wizard's other moments screen
    *
    */
  def chooseOtherMoment(moment: NineCardsMoment): TaskService[Unit]

}
