package ru.maksimfedin.autowireExperiments

import autowire._
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


trait Api {
    def doThing(i: Int, s: String): List[String]
}

object ApiImpl extends Api {
    def doThing(i: Int, s: String): List[String] = List.fill(i)(s)
}

object Server extends autowire.Server[String, Decoder, Encoder] {

    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    override def write[Result: Encoder](r: Result): String = r.asJson.toString


    override def read[Result](r: String)(implicit ev: Decoder[Result]): Result = decode[Result](r) match {
        case Right(value) => value
        case Left(_) => throw new Exception
    }

    val routes: Server.Router = Server.route[Api](ApiImpl)
}

object Client extends autowire.Client[String, Decoder, Encoder] {

    override def write[Result: Encoder](r: Result): String = r.asJson.toString

    override def read[Result](r: String)(implicit ev: Decoder[Result]): Result = decode[Result](r) match {
        case Right(value) => value
        case Left(_) => throw new Exception
    }

    override def doCall(req: Request): Future[String] = Server.routes.apply(req)

}

object Circe extends App {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    println(Await.result(Client[Api].doThing(3, "test").call(), Duration.Inf))
}

// List(test,test,test)
