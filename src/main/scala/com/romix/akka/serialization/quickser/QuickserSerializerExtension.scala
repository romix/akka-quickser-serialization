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

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import com.typesafe.config.Config

import akka.actor.{ ActorSystem, Extension, ExtendedActorSystem, Address, DynamicAccess }
import akka.event.Logging
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.ArrayBuffer
import java.io.NotSerializableException

object QuickserSerialization {

  class Settings(val config: Config) {
  
    import scala.collection.JavaConverters._
    import config._

	// type can be: graph, simple
    val SerializerType: String = if(config.hasPath("akka.actor.quickser.type")) config.getString("akka.actor.quickser.type") else "graph"

    val BufferSize: Int = if(config.hasPath("akka.actor.quickser.buffer-size")) config.getInt("akka.actor.quickser.buffer-size") else 4096
	
	// Each entry should be: FQCN -> integer id
    val ClassNameMappings: Map[String, String] = if (config.hasPath("akka.actor.quickser.mappings")) configToMap(getConfig("akka.actor.quickser.mappings")) else Map[String, String]()

    val ClassNames: java.util.List[String] = if (config.hasPath("akka.actor.quickser.classes")) config.getStringList("akka.actor.quickser.classes") else new java.util.ArrayList()
	
	// Strategy: default, explicit, incremental
    val IdStrategy: String = if(config.hasPath("akka.actor.protostuff.idstrategy")) config.getString("akka.actor.protostuff.idstrategy") else "default"

    private def configToMap(cfg: Config): Map[String, String] =
      cfg.root.unwrapped.asScala.toMap.map { case (k, v) => (k, v.toString) }
  }  
}

class QuickserSerialization(val system: ExtendedActorSystem) extends Extension {
  import QuickserSerialization._
	 
  val settings = new Settings(system.settings.config)
  val log = Logging(system, getClass.getName) 
  
}

object QuickserSerializationExtension extends ExtensionId[QuickserSerialization] with ExtensionIdProvider {
  override def get(system: ActorSystem): QuickserSerialization = super.get(system)
  override def lookup = QuickserSerializationExtension
  override def createExtension(system: ExtendedActorSystem): QuickserSerialization = new QuickserSerialization(system)
}