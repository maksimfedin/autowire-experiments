package ru.maksimfedin.slothExperiments


import autowire._

import scala.concurrent.ExecutionContext.Implicits.global

// shared API interface
trait MyApi {
    def doThing(i: Int, s: String): Seq[String]
}

// server-side implementation, and router
object MyApiImpl extends MyApi {
    def doThing(i: Int, s: String) = Seq.fill(i)(s)
}

object MyServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
    def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)

    def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)

    val routes = MyServer.route[MyApi](MyApiImpl)
}

// client-side implementation, and call-site
object MyClient extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer] {
    def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)

    def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)

    override def doCall(req: Request) = {
        println(req)
        MyServer.routes.apply(req)
    }
}

object Main extends App {
    MyClient[MyApi].doThing(3, "lol").call().foreach(println)
}

// List(lol, lol, lol)
