import scala.util.Try

import nebula4scala.data.input.Context

package nebula4scala {

  object syntax {
    implicit val tryCtx: Context[Try] = new Context[Try] {}

    trait ResultSetHandler[F[_]] {
      def handle(result: Any): F[Any]
    }

  }
}
