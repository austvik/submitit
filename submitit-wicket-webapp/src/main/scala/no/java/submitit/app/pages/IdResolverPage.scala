package no.java.submitit.app.pages

import org.apache.wicket.markup.html.WebPage
import no.java.submitit.common._
import no.java.submitit.model._
import no.java.submitit.app.State
import org.apache.wicket.markup.html.basic._


class IdResolverPage extends LayoutPage {

  val id = getRequest.getParameter("id")

  val presentation: Presentation =
    try {
      val backendClient = State.get.backendClient
      backendClient.loadPresentation(id)
    }
  catch {
    case x => null
  }
  
  if(presentation != null) {
    State.get.fromServer = true 
    State.get.currentPresentation = presentation
  }
  
  val (text, doRedirect) = if(id == null) ("you must supply and id", false) else if (presentation == null) ("not a valid key", false) else ("Redirecting", true) 
  add(new Label("identified", text))
  if(doRedirect) setResponsePage(new ReviewPage(presentation))
  
}
