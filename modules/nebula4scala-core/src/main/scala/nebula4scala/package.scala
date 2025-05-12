package nebula4scala {

  import scala.concurrent._

  import nebula4scala.data.input.Context

  object syntax {
    type ScalaFuture[T] = Future[T]
    implicit val ec: ExecutionContext          = scala.concurrent.ExecutionContext.Implicits.global
    implicit val context: Context[ScalaFuture] = new Context[ScalaFuture] {}

    trait ResultSetHandler[F[_]] {
      def handle(result: Any): F[Any]
    }
  }
}
