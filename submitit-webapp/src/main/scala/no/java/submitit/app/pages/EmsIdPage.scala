/*
 * Copyright 2009 javaBin
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

package no.java.submitit.app.pages

import no.java.submitit.common.LoggHandling
import no.java.submitit.app.State
import org.apache.wicket.markup.html.basic.Label

class EmsIdPage extends LayoutPage with LoggHandling {

  val id = getRequest.getParameter("id")
  val p = State().backendClient.loadPresentation(id)
  if(p.isDefined) {
    setResponsePage(new ReviewPage(p.get, true, true))
  }
  else {
    contentBorder.add(new Label("identified", "Could not find presentation with ems-id: " + id))
  }


}