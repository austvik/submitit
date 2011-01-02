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

package no.java.submitit.ems

import no.java.ems.client._
import no.java.ems.domain.{Event,Session,Person,EmailAddress}
import _root_.java.io.Serializable
import scala.collection.JavaConversions._
import no.java.submitit.common.Implicits._
import no.java.submitit.common._
import no.java.submitit.model._

class EmsClient(eventName: String, serverUrl: String, username: Option[String], password: Option[String], tags: List[String]) extends BackendClient with Serializable {

  def emsService = {
    val service = new RestEmsService(serverUrl)
    if (username.isDefined && password.isDefined) {
      service.setCredentials(username.get, password.get)
    }
    service
  }
  
  def converter = new EmsConverter
  
  lazy val event = findOrCreateEvent(eventName, emsService.getEvents().toList)
  
  def savePresentation(presentation: Presentation): String = {
    presentation.speakers.foreach(speaker => {
      val person = findOrCreateContact(speaker)
      updateDefaultEmail(person, speaker.email)
      speaker.personId = person.getId
      if (speaker.hasNewPicture) saveBinary(speaker.picture.get)
    })

    val attachments = presentation.slideset.toList ::: presentation.pdfSlideset.toList
    attachments.filter(_.isNew).foreach(saveBinary)
    updateOrCreateSession(presentation).sessionId
  }
  
  def loadPresentation(id: String): Option[Presentation] = {
    getSession(id) match {
      case Some(session) => {
        val presentation = converter.toPresentation(session)
        presentation.speakers.foreach(speaker => {
          val person = emsService.getContact(speaker.personId)
          speaker.email = person.getEmailAddresses.toList.head.getEmailAddress 
        })
        Some(presentation)
      }
      case None => None
    }
  }

  private def getSession(id: String): Option[Session] = {
    // Workaround for authorization problems...
    val sessions = emsService.getSessions(event.getId)
    sessions.find(session => session.getId == id)
  }
  
  private def updateOrCreateSession(presentation: Presentation): Presentation = {
    val session = 
      if (presentation.sessionId == null) {	
        val s = new Session()
        s.addTags(tags)
        s
      }
      else getSession(presentation.sessionId) match {
        case Some(session) => session
        case None => error("Unknown session " + presentation.sessionId)
      } 
    
    session.setEventId(event.getId)
    converter.updateSession(presentation, session)
    emsService.saveSession(session)
    
    presentation.sessionId = session.getId
    presentation
  }
  
  private def findOrCreateContact(speaker: Speaker): Person = {
    if (speaker.personId != null) findContactById(speaker)
    else findContactByEmail(speaker.email) match {
      case Some(person) => person
      case None => findContactByName(speaker.name) match {
        case Some(person) => person
        case None => createContact(speaker)
      }
    }
  }

  /**
   * User may have modified speaker form (renamed speaker and given a new email addresse, which would be a new speaker.
   * However the speaker would have the same id because of the way the form is created. Must handle this and
   * create a new speaker if speaker email and name does not match.
   */
  private def findContactById(speaker: Speaker): Person = {
    val contact = emsService.getContact(speaker.personId)
    if(emailMatches(contact, speaker.email) || contact.getName == speaker.name) contact
    else createContact(speaker)
  }

  private def emailMatches(contact: Person, email: String) = {
      contact.getEmailAddresses.exists(adr =>
        adr.getEmailAddress() == email)
  }
  
  private def findContactByEmail(email: String): Option[Person] = {
    emsService.getContacts().find(emailMatches(_, email))
  }
  
  private def findContactByName(name: String): Option[Person] = {
    // TODO
    None
  }

  private def updateDefaultEmail(person: Person, email: String) {
    val emailAddresses = person.getEmailAddresses.toList
    if (emailAddresses.head.getEmailAddress != email) {
      person.setEmailAddresses(new EmailAddress(email) :: 
                                 emailAddresses.filter(_.getEmailAddress != email))
      emsService.saveContact(person)
    }
  }
  
  private def createContact(speaker: Speaker): Person = {
    val person = converter.toPerson(speaker)
    emsService.saveContact(person)
    person
  }
  
  private def saveBinary(binary: Binary) {
    val emsBinary = converter.toEmsBinary(binary)
    val result = emsService.saveBinary(emsBinary)
    binary.id = result.getId
  }
  
  private def findOrCreateEvent(name: String, events: List[Event]): Event = events.find(_.getName == name).getOrElse(createEvent(name))
  
  private def createEvent(name: String): Event = {
    val event = new Event()
    event.setName(name)
    emsService.saveEvent(event)
    event
  }
  
}