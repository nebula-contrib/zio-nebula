package nebula4scala.example.cats.effect

import cats.effect._
import cats.effect.std.Console

import nebula4scala.Configs
import nebula4scala.cats.effect._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data.input.Stmt

object NebulaSessionClientExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- NebulaSessionClient.resource[IO](Configs.config()).use { client =>
        for {
          resM1 <- client.execute(Stmt.str[IO]("""
                                                |INSERT VERTEX person(name, age) VALUES 
                                                |'Bob':('Bob', 10), 
                                                |'Lily':('Lily', 9),'Tom':('Tom', 10),
                                                |'Jerry':('Jerry', 13),
                                                |'John':('John', 11);""".stripMargin))
          res1  <- resM1.errorMessageM
          _     <- Console[IO].print(res1)
          resM2 <- client.execute(Stmt.str[IO]("""
                                                 |INSERT EDGE like(likeness) VALUES
                                                 |'Bob'->'Lily':(80.0),
                                                 |'Bob'->'Tom':(70.0),
                                                 |'Lily'->'Jerry':(84.0),
                                                 |'Tom'->'Jerry':(68.3),
                                                 |'Bob'->'John':(97.2);""".stripMargin))
          res2  <- resM2.errorMessageM
          _     <- Console[IO].print(res2)
          resM3 <- client.execute(Stmt.str[IO]("""
                                                 |USE test;
                                                 |MATCH (p:person) RETURN p LIMIT 4;
                                                 |""".stripMargin))
          res3 <- resM3.rowsM
          _    <- Console[IO].print(res3.toString())
        } yield ()
      }
    } yield ExitCode.Success
  }
}
