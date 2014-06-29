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

import scala.concurrent.stm.Ref


sealed abstract class ConnStatusM

case class AConnected(msg: String = "") extends ConnStatusM

case class ADisconnected(msg: String = "") extends ConnStatusM

sealed abstract class ConnCmdM

case class AConnect(msg: String = "") extends ConnCmdM

case class ADisconnect(msg: String = "") extends ConnCmdM

private[scalamqtt] class ConnectionActor(host: String, port: Int,
                                         recipient: ActorRef) extends Actor with ActorLogging {
  implicit val system = context.system

  val addr: Ref[Option[InetSocketAddress]] = Ref(None)

  val manager: Ref[Option[ActorRef]] = Ref(None)

  val tcpConnection: Ref[Option[ActorRef]] = Ref(None)

  def receive = {

    case c: AConnect =>

      try {

        val inAddr = new InetSocketAddress(host, port)

        val ret = IO(Tcp)

        ret ! Connect(inAddr)

        addr.single.set(Some(inAddr))

        manager.single.set(Some(ret))

        context become register

        println("here")

      } catch {

        case e: IllegalArgumentException => sender() ! ADisconnected(e.toString)
          log.error(e.toString)

        case e: SecurityException => sender() ! ADisconnected(e.toString)
          log.error(e.toString)

        case e:Exception => sender() ! ADisconnected(e.toString)
      }

    case _ => //ignore
  }


  def register: PartialFunction[Any, Unit] = {

    case CommandFailed(_: Connect) => {
      println("command failed")
      context stop self
    }

    case c@Connected(remote, local) => {

      println("here2")
      sender() ! Tcp.Register(self)

      val connection = sender()

      tcpConnection.single.set(Some(connection))

      context become onConnect()

      recipient ! AConnected("Connection successful...")
    }

    case x@_ => println(x) //ignore



  }

  def onConnect(): PartialFunction[Any, Unit] = {

    case data: ByteString =>

      tcpConnection.single.get.map{ tcp => tcp ! Write(data) }

    case CommandFailed(w: Write) =>

      // O/S buffer was full
      recipient ! "write failed"

    case Received(data) =>

      recipient ! data

    case _: ConnectionClosed =>

      recipient ! ADisconnected("connection closed")

      context stop self

    case d: ADisconnect => tcpConnection.single.get.map{tcp =>  tcp ! Close}

    case _ => //ignore

  }
}
