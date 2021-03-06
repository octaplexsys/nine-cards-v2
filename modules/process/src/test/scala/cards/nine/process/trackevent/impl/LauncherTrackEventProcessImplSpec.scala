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

package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.LauncherTrackEventTestData
import cards.nine.models.types.{AppCategory, Game}
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait LauncherTrackEventProcessSpecification
    extends TaskServiceSpecification
    with LauncherTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class LauncherTrackEventProcessImplSpec extends LauncherTrackEventProcessSpecification {

  "openAppFromAppDrawer" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(openAppEntertainmentEvent)
    }

    "track the app with the right parameters when the package is a game" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openAppFromAppDrawer(gamePackageName, gameCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(openAppGameEvent)
      there was one(mockTrackServices).trackEvent(
        openAppGameEvent.copy(category = AppCategory(Game)))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process
        .openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory)
        .mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openAppEntertainmentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception in the second call" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns (serviceRight(Unit), serviceLeft(
        trackServicesException))

      process.openAppFromAppDrawer(gamePackageName, gameCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openAppGameEvent)
      there was one(mockTrackServices).trackEvent(
        openAppGameEvent.copy(category = AppCategory(Game)))
    }

  }

}
