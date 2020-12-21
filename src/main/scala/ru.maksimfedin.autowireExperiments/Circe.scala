package ru.maksimfedin.autowireExperiments

import autowire._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Api {
    def doThing(i: Int, s: String): List[String]
}

object ApiImpl extends Api {
    def doThing(i: Int, s: String): List[String] = List.fill(i)(s)
}

object Server extends autowire.Server[String, Decoder, Encoder] {

    override def write[Result: Encoder](r: Result): String = {
        println(s"Write ${r}")
        val y =  r.asJson
        println(s"Write asJson  ${y}")
        val z =  y.toString()
        println(s"Write toString  ${z}")
        z
    }


    override def read[Result](r: String)(implicit ev: Decoder[Result]): Result = {

        ev.decodeJson(r.asJson) match {
            case Right(value) =>
                println(s"Read ${value}")
                value
            case Left(_) => throw new Exception
        }
    }

    val routes: Server.Router = Server.route[Api](ApiImpl)
}

object Client extends autowire.Client[String, Decoder, Encoder] {

    override def write[Result: Encoder](r: Result): String = {
        r.asJson.toString()
    }

    override def read[Result](r: String)(implicit ev: Decoder[Result]): Result = {
        ev.decodeJson(r.asJson)
        match {
            case Right(value) => value
            case Left(_) => throw new Exception
        }
    }

    override def doCall(req: Request): Future[String] = {
        println(req)
        Server.routes.apply(req)
    }

}

object Circe extends App {
    Client[Api].doThing(3, "test").call().foreach(println)
}

// List(test,test,test)
