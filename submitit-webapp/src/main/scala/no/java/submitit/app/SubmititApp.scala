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

package no.java.submitit.app

import no.java.submitit.common._
import no.java.submitit.ems._
import no.java.submitit.app.pages._
import org.apache.wicket.util.lang.Bytes
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest
import org.apache.wicket.protocol.http.WebRequest
import javax.servlet.http.HttpServletRequest
import _root_.java.util.Properties
import org.apache.wicket.Application._
import org.apache.wicket.settings.IExceptionSettings
import Functions._
import no.java.submitit.config.Keys._
import no.java.submitit.config.{Keys, ConfigKey}
import org.apache.wicket._
import authorization.IUnauthorizedComponentInstantiationListener

class SubmititApp extends WebApplication with LoggHandling {

  override def init() {
    SubmititApp.propertyFileName = System.getProperty("submitit.properties")
    if (SubmititApp.propertyFileName == null) throw new Exception("""You must specify "submitit.properties" as a system property.""")
    val props = PropertyIOUtils.loadRessource(SubmititApp.propertyFileName)
    
    SubmititApp.properties = DefaultConfigValues.mergeConfig(props)

    mountBookmarkablePage("/lookupPresentation", classOf[IdResolverPage]);
    mountBookmarkablePage("/proposal", classOf[IdResolverPage]);
    mountBookmarkablePage("/ems", classOf[EmsIdPage]);
    mountBookmarkablePage("/help", classOf[HelpPage]);
    mountBookmarkablePage("/admin-login", classOf[admin.AdminLogin])
    mountBookmarkablePage("/invitation", classOf[InvitationPage])
    getApplicationSettings.setDefaultMaximumUploadSize(Bytes.kilobytes(500))

    val securitySettings = getSecuritySettings()
    securitySettings.setAuthorizationStrategy(new PageAuthenticator())
    securitySettings.setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {
      override def onUnauthorizedInstantiation(component: Component) {
        throw new RestartResponseAtInterceptPageException(new EmsIdLoginPage())
      }
    })
  }


  private def backendClient: BackendClient = {
    if (SubmititApp.getSetting(emsUrl).isDefined) new EmsClient(SubmititApp.getSetting(eventName).get, 
                                                                SubmititApp.getSetting(emsUrl).get, 
                                                                SubmititApp.getSetting(emsUser), SubmititApp.getSetting(emsPwd), 
                                                                SubmititApp.getListSetting(commaSeparatedListOfTagsForNewSubmissions))
    else no.java.submitit.common.BackendClientMock
  }

  override def newWebRequest(servletRequest: HttpServletRequest) = new UploadWebRequest(servletRequest)
  
  override def newSession(request: Request, response: Response):State = new State(request, backendClient)
  
  def getHomePage = classOf[StartPage]
  
  override def newRequestCycle(request: Request, response: Response) = new MyRequestCycle(this, request.asInstanceOf[WebRequest], response)
  
}

class MyRequestCycle(application: WebApplication, request: WebRequest, response: Response) extends org.apache.wicket.protocol.http.WebRequestCycle(application, request, response) {
  override def onRuntimeException(cause: org.apache.wicket.Page, e: RuntimeException) = {
    if ("deployment" == getApplication.getConfigurationType)
      new ErrorPage(State().currentPresentation, e)
    else super.onRuntimeException(cause, e)
  }
  
  override def onBeginRequest(){
  	val webRequest = request.asInstanceOf[WebRequest]
    val session = webRequest.getHttpServletRequest.getSession
    session.setAttribute(binariesInSession, State().binaries)
  }
  
}

object SubmititApp {
  
  private var properties: collection.Map[ConfigKey, Option[String]] = _
  private var propertyFileName: String = _

  def props: collection.Map[ConfigKey, Option[String]] = properties
  
  def props_=(props: collection.Map[ConfigKey, Option[String]]) {
    this.properties = props
    PropertyIOUtils.writeResource(propertyFileName, props)
  }
  
  def getSetting(key: ConfigKey) = props.get(key).get

  def setting(key: ConfigKey) = props.get(key).get.get
  
  def getBccEmailList = {
    getSetting(emailBccCommaSeparatedList) match {
      case Some(email) => email.split(",")
      case None => new Array[String](0)
    }
  }
  
  def getOfficialEmail = {
    getSetting(officialEmailReplyTo).get
  }
  
  def intSetting(key: ConfigKey) = intParse(getSetting(key).get)
  
  def boolSetting(key: ConfigKey) = booleanParse(getSetting(key).get)
  
  def getListSetting(key: ConfigKey, separator: Char) = getSetting(key) match {
    case Some(s) => s.split(separator).toList.map(_.trim)
    case None => Nil
  }

  def getListSetting(key: ConfigKey): List[String] = getListSetting(key, ',')
  
  def authenticates(password: Object) = getSetting(adminPassPhrase).get == password
  
}
