package nebula4scala.cats

import scala.collection.compat.immutable.LazyList

import cats.effect.Async
import cats.syntax.all._

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.api._
import nebula4scala.cats.NebulaResultSetImpl.NebulaRecordImpl
import nebula4scala.data.value._
import nebula4scala.syntax._

final class NebulaResultSetImpl[F[_]: Async](underlying: NebulaResultSet[ScalaFuture]) extends NebulaResultSet[F] {

  def isSucceededM: F[Boolean] = Async[F].fromFuture(Async[F].delay(underlying.isSucceededM))

  def isEmptyM: F[Boolean] = Async[F].fromFuture(Async[F].delay(underlying.isEmptyM))

  def errorCodeM: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.errorCodeM))

  def spaceNameM: F[String] = Async[F].fromFuture(Async[F].delay(underlying.spaceNameM))

  def errorMessageM: F[String] = Async[F].fromFuture(Async[F].delay(underlying.errorMessageM))

  def commentM: F[String] = Async[F].fromFuture(Async[F].delay(underlying.commentM))

  def latencyM: F[Long] = Async[F].fromFuture(Async[F].delay(underlying.latencyM))

  def planDescM: F[PlanDescription] = Async[F].fromFuture(Async[F].delay(underlying.planDescM))

  def keysM: F[List[String]] = Async[F].fromFuture(Async[F].delay(underlying.keysM))

  def columnNamesM: F[List[String]] = Async[F].fromFuture(Async[F].delay(underlying.columnNamesM))

  def rowsSizeM: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.rowsSizeM))

  def rowValuesM(index: Int): F[NebulaRecord[F]] =
    Async[F].fromFuture(Async[F].delay(underlying.rowValuesM(index))).map(r => new NebulaRecordImpl(r))

  def colValuesM(columnName: String): F[LazyList[ValueWrapper]] =
    Async[F].fromFuture(Async[F].delay(underlying.colValuesM(columnName)))

  def rowsM: F[List[Row]] = Async[F].fromFuture(Async[F].delay(underlying.rowsM))
}

object NebulaResultSetImpl {

  final class NebulaRecordImpl[F[_]: Async](private val underlying: NebulaRecord[ScalaFuture]) extends NebulaRecord[F] {
    override def iteratorM: F[Iterator[ValueWrapper]] = Async[F].fromFuture(Async[F].delay(underlying.iteratorM))

    override def foreachM[U](f: ValueWrapper => U): F[Unit] =
      Async[F].fromFuture(Async[F].delay(underlying.foreachM(f)))

    override def getM(index: Int): F[ValueWrapper] = Async[F].fromFuture(Async[F].delay(underlying.getM(index)))

    override def getM(columnName: String): F[ValueWrapper] =
      Async[F].fromFuture(Async[F].delay(underlying.getM(columnName)))

    override def valuesM: F[LazyList[ValueWrapper]] = Async[F].fromFuture(Async[F].delay(underlying.valuesM))

    override def sizeM: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.sizeM))

    override def containsM(columnName: String): F[Boolean] =
      Async[F].fromFuture(Async[F].delay(underlying.containsM(columnName)))
  }
}
