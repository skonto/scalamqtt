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

package org.scalamqtt.impl.comm.akka

import java.net.InetSocketAddress

import akka.actor.{ActorLogging, ActorRef, Actor}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

private[scalamqtt] class ConnectionActor(addr:InetSocketAddress, recipient: ActorRef) extends Actor with ActorLogging{
  implicit val system = context.system

  val conn = IO(Tcp)

  conn ! Connect(addr)


  def receive ={

    case CommandFailed(_: Connect) => {
      val host = remote.toString
      context stop self
    }

    case c @ Connected(remote, local) =>
    {

      sender() ! Tcp.Register(self)

      val connection = sender()

      context become {
        case data: ByteString =>
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          recipient ! "write failed"
        case Received(data) =>
          recipient ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          recipient ! "connection closed"
          context stop self
      }
    }
  }
}
