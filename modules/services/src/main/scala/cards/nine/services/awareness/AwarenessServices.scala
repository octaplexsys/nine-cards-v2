/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.awareness

import android.content.BroadcastReceiver
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.AwarenessFenceUpdate
import cards.nine.models.{Headphones, Location, ProbablyActivity, WeatherState}

trait AwarenessServices {

  /**
   * Return the most probably activity
   *
   * @return activity
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getTypeActivity: TaskService[ProbablyActivity]

  /**
   * Register a pending intent for fence updates
   * @param action the action for the intent
   * @param fences fences to register for
   * @param receiver that will receive the updates
   */
  def registerFenceUpdates(
      action: String,
      fences: Seq[AwarenessFenceUpdate],
      receiver: BroadcastReceiver)(implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
   * Register a pending intent for fence updates
   * @param action the action for the intent
   */
  def unregisterFenceUpdates(action: String)(
      implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
   * Return headphone state
   *
   * @return if headphone is connected
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getHeadphonesState: TaskService[Headphones]

  /**
   * Return information about current location
   *
   * @return current location
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getLocation(implicit contextSupport: ContextSupport): TaskService[Location]

  /**
   * Return information about current weather
   *
   * @return current weather
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getWeather: TaskService[WeatherState]

}
