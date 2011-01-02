/*
 * Copyright 2011 javaBin
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package no.java.submitit.common

import _root_.java.io.Serializable

import scala.collection.mutable.Map

import no.java.submitit.model._

object BackendClientMock extends BackendClient with Serializable {

  var nextId = 1
  
  val presentations = Map.empty[String, Presentation]
  val speakers = Map.empty[String, Speaker]

  def savePresentation(pres: Presentation): String = {
    if (pres.sessionId == null) {
      pres.sessionId = nextId.toString
      nextId = nextId + 1
    }
    for(speaker <- pres.speakers){
      speakers(speaker.email) = speaker
    }
    presentations(pres.sessionId) = pres
    pres.sessionId
  }
  
  def loadPresentation(id: String) = presentations get id
}
