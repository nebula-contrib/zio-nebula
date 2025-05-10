package nebula4scala {

  import scala.concurrent.{ ExecutionContext, Future }

  import nebula4scala.data.input.Context

  object syntax {
    type SyncFuture[T] = Future[T]
    implicit val ec: ExecutionContext         = scala.concurrent.ExecutionContext.Implicits.global
    implicit val context: Context[SyncFuture] = new Context[SyncFuture] {}
  }
}
