/*
 * Copyright 2009 JavaBin
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

import org.apache.wicket.markup.html.basic._
import DefaultConfigValues._

class StartPage extends LayoutPage {
  
  if (SubmititApp.boolSetting(submitAllowedBoolean)) {
    
    if(State().isNew) {
      setResponsePage(new SubmitPage(State().currentPresentation))
    }
    else {
      setResponsePage(new ReviewPage(State().currentPresentation))
    }
  }
  else {
    val res = new Label("info", SubmititApp.getSetting(submitNotAllowedHtml))
    res.setEscapeModelStrings(false)
    add(res)
  }

}
