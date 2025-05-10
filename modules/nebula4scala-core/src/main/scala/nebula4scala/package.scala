import scala.concurrent.Future

package object nebula4scala {
  type SyncFuture[T] = Future[T]

}
