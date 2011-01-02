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

import _root_.java.util.Properties
import _root_.java.io._
import no.java.submitit.common.IOUtils._
import no.java.submitit.config.ConfigKey

object PropertyIOUtils {
	
  def loadRessource(resourceName: String) = {
    val file = new File(resourceName)
    using(new BufferedInputStream(new FileInputStream(file))) { stream =>
      val p = new Properties();
      p.load(stream);
      p
    }
  }
  
  def writeResource(fileName: String, props: collection.Map[ConfigKey, Option[String]]) {
    val file = new File(fileName)
    val transformed = props.map{case (key, value) => key + "=" + value.getOrElse("")}.toList
    val propertyString = transformed.mkString("", "\n", "")
    using(new BufferedWriter(new FileWriter(file))) { stream =>
      propertyString.foreach(stream.write(_))
      stream.flush
    }
  }

}