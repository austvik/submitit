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

package no.java.submitit.app.pages

import borders.ContentBorder
import org.apache.wicket.markup.html.link.ExternalLink
import no.java.submitit.app.SubmititApp
import org.apache.wicket.{MarkupContainer, Component}

class HelpPage extends LayoutPage {

  OfficialEmailLink.addLink(contentBorder)
  
}

object OfficialEmailLink {
  
  def addLink(container: MarkupContainer) {
    container.add(new ExternalLink("officialEmail", "mailto:" + SubmititApp.getOfficialEmail, SubmititApp.getOfficialEmail))
  }
  
}
