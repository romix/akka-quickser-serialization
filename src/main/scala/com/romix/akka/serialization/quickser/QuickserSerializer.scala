/*******************************************************************************
 * Copyright 2012 Roman Levenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.romix.akka.serialization.quickser

import akka.serialization._
import akka.actor.ExtendedActorSystem
import akka.event.Logging

import com.romix.quickser.Serialization
import com.typesafe.config.ConfigFactory
import java.util.Map
import scala.collection.JavaConversions._

import java.lang.System

import QuickserSerialization._

import com.romix.quickser.Serialization

class QuickserSerializer (val system: ExtendedActorSystem) extends Serializer {

import QuickserSerialization._
	val log = Logging(system, getClass.getName) 

	val serializer = new Serialization()

	val settings = new Settings(system.settings.config)

	val classes = settings.ClassNames 
	
	locally {
		// Register classes
		for(classname <- classnames) {
			// Load class
			system.dynamicAccess.getClassFor[AnyRef](classname) match {
			case Right(clazz) => serializer.registerClass(clazz)
			case Left(e) => { 
				log.warning("Class could not be loaded and/or registered: {} ", classname) 
				/* throw e */ 
				}
			}
		}	
	}

	// This is whether "fromBinary" requires a "clazz" or not
	def includeManifest: Boolean = false

	// A unique identifier for this Serializer
	def identifier = 123454322

		
	// "toBinary" serializes the given object to an Array of Bytes
	def toBinary(obj: AnyRef): Array[Byte] = {
		serializer.serialize(obj)
	}

	// "fromBinary" deserializes the given array,
	// using the type hint (if any, see "includeManifest" above)
	// into the optionally provided classLoader.
	def fromBinary(bytes: Array[Byte], clazz: Option[Class[_]]): AnyRef = {
		serializer.deserialize(bytes)
	}
}
