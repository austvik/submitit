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

package no.java.submitit.model
import common.IOUtils._

import _root_.java.io._

class Binary private(var id: String, val name: String, val contentType: String) extends Serializable {

  var tmpFileName: Option[String] = None
  
  def hasContent = tmpFileName.isDefined
  def isNew = id == null
 
  def this(name: String, contentType: String) = 
    this(null, name, contentType)
  
  def content: Option[Array[byte]] = {
    if (tmpFileName.isDefined) {
      var res = List[Byte]()
    	using(new BufferedInputStream(new FileInputStream(new File(tmpFileName.get)))) { stream =>
    		var value = stream.read
    		while(value != -1) {
    			res = value.toByte :: res
    		  value = stream.read
    		}
    	}
      Some(res.reverse.toArray)
    }
    else None
  }
    
  private def content(inputStream: InputStream) {
   val tempFile = File.createTempFile(name, ".tmp");
   
    usingIS(new BufferedInputStream(inputStream)) { 
      istream => 
      using(new BufferedOutputStream(new FileOutputStream(tempFile))) { ustream =>
        var current: Int = istream.read()
        while(current != -1) {
          ustream.write(current)
          current = istream.read()
        }
      }
    }
   
   tmpFileName = Some(tempFile.getCanonicalPath)
  }
  
}

object Binary {
  
	def apply(name: String, contentType: String, content: Option[InputStream]): Binary = apply(null, name, contentType, content) 
         	
  def apply(id: String, name: String, contentType: String, content: Option[InputStream]) = {
    val res = new Binary(id, name, contentType)
    content match {
      case Some(stream) => res.content(stream)
      case None =>
    }
    res
  }
  
}

