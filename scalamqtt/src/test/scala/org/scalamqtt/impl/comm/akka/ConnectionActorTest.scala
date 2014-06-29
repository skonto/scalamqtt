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
 * Date: 28/6/2013
 */

package org.scalamqtt.impl.comm.akka

import akka.actor.{Props, Actor, ActorSystem}
import akka.util.ByteString
import org.junit.runner.RunWith
import org.scalamqtt.common.Server
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}


@RunWith(classOf[JUnitRunner])
class ConnectionActorTest extends FunSuite with Matchers with BeforeAndAfterAll {

  val actorSystem = ActorSystem("Test")

  val host = "localhost"

  val port = 1020

  val server = actorSystem.actorOf(Props(new Server(host, port)))

  override def afterAll() {

    actorSystem.shutdown()
  }

  test("An actor is created") {

    val l = actorSystem.actorOf(Props(new Listener(host,port)))

   // implicit val timeout : Timeout  = Timeout(10, TimeUnit.SECONDS)

    l ! "connect"

    Thread.sleep(1000)

  }

}


class Listener(host:String, port:Int) extends Actor {

  val conn = context.system.actorOf(Props(new ConnectionActor(host, port, self)))

  def receive() = {

    case "connect" =>

      conn ! AConnect()

    case a:AConnected => conn ! ByteString("Hello".getBytes())

    case y:ByteString => println("Listener: "+ y.utf8String)

    case v@_ => println("Listener(unknown message):" + v)

  }

}