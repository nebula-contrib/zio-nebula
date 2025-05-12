package nebula4scala.cats.effect

import scala.collection.compat.immutable.LazyList

import cats.effect.Async
import cats.syntax.all._

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import NebulaResultSetImpl.NebulaRecordImpl
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data.value._
import nebula4scala.syntax._

final class NebulaResultSetImpl[F[_]: Async](underlying: NebulaResultSet[ScalaFuture]) extends NebulaResultSet[F] {

  def isSucceededM: F[Boolean] = implicitly[Effect[F]].fromFuture(underlying.isSucceededM)

  def isEmptyM: F[Boolean] = implicitly[Effect[F]].fromFuture(underlying.isEmptyM)

  def errorCodeM: F[Int] = implicitly[Effect[F]].fromFuture(underlying.errorCodeM)

  def spaceNameM: F[String] = implicitly[Effect[F]].fromFuture(underlying.spaceNameM)

  def errorMessageM: F[String] = implicitly[Effect[F]].fromFuture(underlying.errorMessageM)

  def commentM: F[String] = implicitly[Effect[F]].fromFuture(underlying.commentM)

  def latencyM: F[Long] = implicitly[Effect[F]].fromFuture(underlying.latencyM)

  def planDescM: F[PlanDescription] = implicitly[Effect[F]].fromFuture(underlying.planDescM)

  def keysM: F[List[String]] = implicitly[Effect[F]].fromFuture(underlying.keysM)

  def columnNamesM: F[List[String]] = implicitly[Effect[F]].fromFuture(underlying.columnNamesM)

  def rowsSizeM: F[Int] = implicitly[Effect[F]].fromFuture(underlying.rowsSizeM)

  def rowValuesM(index: Int): F[NebulaRecord[F]] =
    implicitly[Effect[F]].fromFuture(underlying.rowValuesM(index)).map(r => new NebulaRecordImpl(r))

  def colValuesM(columnName: String): F[LazyList[ValueWrapper]] =
    implicitly[Effect[F]].fromFuture(underlying.colValuesM(columnName))

  def rowsM: F[List[Row]] = implicitly[Effect[F]].fromFuture(underlying.rowsM)
}

object NebulaResultSetImpl {

  final class NebulaRecordImpl[F[_]: Async](private val underlying: NebulaRecord[ScalaFuture]) extends NebulaRecord[F] {

    override def iteratorM: F[Iterator[ValueWrapper]] =
      implicitly[Effect[F]].fromFuture(underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): F[Unit] =
      implicitly[Effect[F]].fromFuture(underlying.foreachM(f))

    override def getM(index: Int): F[ValueWrapper] = implicitly[Effect[F]].fromFuture(underlying.getM(index))

    override def getM(columnName: String): F[ValueWrapper] =
      implicitly[Effect[F]].fromFuture(underlying.getM(columnName))

    override def valuesM: F[LazyList[ValueWrapper]] = implicitly[Effect[F]].fromFuture(underlying.valuesM)

    override def sizeM: F[Int] = implicitly[Effect[F]].fromFuture(underlying.sizeM)

    override def containsM(columnName: String): F[Boolean] =
      implicitly[Effect[F]].fromFuture(underlying.containsM(columnName))
  }
}
