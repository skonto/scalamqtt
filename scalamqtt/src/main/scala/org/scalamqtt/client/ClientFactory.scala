/*
 * (C) Copyright 2014 Stavros Kontopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     st.kontopoulos@gmail.com
 */
package org.scalamqtt.client

import java.util.UUID

import akka.actor.{Actor, ActorSystem}
import org.scalamqtt.impl.comm.akka.ConnectionActor
import org.scalamqtt.impl.protocol.ProtocolActor

import scala.util.Success


object MQTTAkkaClient{

  def apply(host:String, port:String):Either[Throwable,Boolean]= {Success(true)}

}


class MQTTAkkaClient(host:String, port:String){

  private def MQTTAkkaClient (){}

  private val aSystem = ActorSystem(UUID.randomUUID().toString.replace('-','\0'))

  private val conn: ConnectionActor

  private val proto: ProtocolActor

  def shutdown():Either[String, Boolean] ={}

}

class MQTTSecClient{


}
