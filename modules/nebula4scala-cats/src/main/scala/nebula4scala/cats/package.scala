package nebula4scala

package cats {

  object syntax {

    import _root_.cats.Monad
    import nebula4scala.data.input.Context

    implicit def context[F[_]: Monad]: Context[F] = new Context[F] {}
  }
}
