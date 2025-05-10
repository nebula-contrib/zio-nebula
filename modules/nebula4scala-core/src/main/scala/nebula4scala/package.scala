package nebula4scala {

  import scala.concurrent.Future

  import nebula4scala.data.input.Context

  object syntax {
    type SyncFuture[T] = Future[T]
    implicit val context: Context[SyncFuture] = new Context[SyncFuture] {}
  }
}
