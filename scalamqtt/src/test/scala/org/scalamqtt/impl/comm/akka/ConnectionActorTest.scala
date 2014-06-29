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

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.util.{Timeout, ByteString}
import akka.pattern.ask
import org.junit.runner.RunWith
import org.scalamqtt.common.Server
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, BeforeAndAfterAll, FunSuite}

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.stm.Ref


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

    val msg = "blablabla"

    val l = actorSystem.actorOf(Props(new Listener(host,port)))

    implicit val timeout : Timeout  = Timeout(10, TimeUnit.SECONDS)

    Thread.sleep(1000) //this is enough to connect

    val f1= l ? "status"

    val ret1: Boolean = Await.result(f1, Duration(10, TimeUnit.SECONDS)).asInstanceOf[Boolean]

    ret1 should be(true)

    val f2=l ? msg

    val ret2= Await.result(f2, Duration(10, TimeUnit.SECONDS))

    ret2 should be(msg)

  }

}


class Listener(host:String, port:Int) extends Actor {

  val conn = context.system.actorOf(Props(new ConnectionActor(host, port, self)))
  conn ! AConnect()

  val connected: Ref[Boolean] = Ref(false)

  val requester: Ref[Option[ActorRef]]  =  Ref(None)


  def receive() = {

    case "status" => sender() ! connected.single.get

    case data:String => requester.single.set(Some(sender())); conn ! ByteString(data.getBytes())

    case a:AConnected => connected.single.set(true)

    case y:ByteString => val retVal =  y.utf8String
      println("Listener: "+ retVal);  requester.single.get.map{r => r ! retVal}

    case v@_ => println("Listener(unknown message):" + v)

  }

}