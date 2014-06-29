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
import akka.actor.ActorSystem

import org.scalamqtt.impl.comm.akka.ConnectionActor
import org.scalamqtt.impl.protocol.ProtocolActor

import scala.concurrent.stm.Ref


object MQTTAkkaClient{

  def apply(host:String = "localhost", port:Int = 1883, sec:Boolean = false):Either[Throwable,Boolean]= {Right(true)}

}


class MQTTAkkaClient(host:String, port:Int){

  private def MQTTAkkaClient ()={}

  private val aSystem = ActorSystem(UUID.randomUUID().toString.replace('-','\0'))

  private val conn: Ref[Option[ConnectionActor]] = Ref(None)

  private val proto: Ref[Option[ProtocolActor]] = Ref(None)

  def shutdown():Either[String, Boolean] = Right(true)

}

class MQTTSecClient{


}
