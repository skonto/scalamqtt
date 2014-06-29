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
 *
 * Date: 29/6/2013
 */
package org.scalamqtt.common

import java.net.InetSocketAddress

import akka.actor.{Props, Actor}
import akka.io.{IO, Tcp}


class Server(host:String= "localhost", port:Int= 0) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress(host, port))

  def receive = {
    case b @ Bound(localAddress) =>

      println("Server started")
    // do some logging or setup ...

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }

}

class SimplisticHandler extends Actor {
  import Tcp._
  def receive = {
    case Received(data) =>println("Server: received data " + data); sender() ! Write(data)
    case PeerClosed => context stop self
  }
}




