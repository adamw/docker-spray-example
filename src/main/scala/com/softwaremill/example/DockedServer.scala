package com.softwaremill.example

import akka.actor.{Props, Actor, ActorSystem}
import spray.routing._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object DockedServer extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  val countersActor = actorSystem.actorOf(Props(new CountersActor()))

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") {
        complete {
          "Hello World!"
        }
      }
    } ~
    path("counter" / Segment) { counterName =>
      get {
        complete {
          (countersActor ? Get(counterName))
            .mapTo[Int]
            .map(amount => s"$counterName is: $amount")
        }
      } ~
      post {
        parameters("amount".as[Int]) { amount =>
          countersActor ! Add(counterName, amount)
          complete {
            "OK"
          }
        }
      }
    }
  }

  class CountersActor extends Actor {
    private var counters = Map[String, Int]()

    override def receive = {
      case Get(counterName) => sender ! counters.getOrElse(counterName, 0)
      case Add(counterName, amount) =>
        val newAmount = counters.getOrElse(counterName, 0) + amount
        counters = counters + (counterName -> newAmount)
    }
  }

  case class Get(counterName: String)
  case class Add(counterName: String, amount: Int)
}
