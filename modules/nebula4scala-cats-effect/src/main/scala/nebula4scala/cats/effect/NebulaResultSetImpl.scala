package nebula4scala.cats.effect

import scala.collection.compat.immutable.LazyList
import scala.util.Try

import cats.effect.Async
import cats.syntax.all._

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.NebulaResultSetImpl.NebulaRecordImpl
import nebula4scala.cats.effect.syntax._
import nebula4scala.data.value._
import nebula4scala.syntax._

final class NebulaResultSetImpl[F[_]: Async](underlying: NebulaResultSet[Try]) extends NebulaResultSet[F] {

  def isSucceededM: F[Boolean] = implicitly[Effect[F]].fromTry(underlying.isSucceededM)

  def isEmptyM: F[Boolean] = implicitly[Effect[F]].fromTry(underlying.isEmptyM)

  def errorCodeM: F[Int] = implicitly[Effect[F]].fromTry(underlying.errorCodeM)

  def spaceNameM: F[String] = implicitly[Effect[F]].fromTry(underlying.spaceNameM)

  def errorMessageM: F[String] = implicitly[Effect[F]].fromTry(underlying.errorMessageM)

  def commentM: F[String] = implicitly[Effect[F]].fromTry(underlying.commentM)

  def latencyM: F[Long] = implicitly[Effect[F]].fromTry(underlying.latencyM)

  def planDescM: F[PlanDescription] = implicitly[Effect[F]].fromTry(underlying.planDescM)

  def keysM: F[List[String]] = implicitly[Effect[F]].fromTry(underlying.keysM)

  def columnNamesM: F[List[String]] = implicitly[Effect[F]].fromTry(underlying.columnNamesM)

  def rowsSizeM: F[Int] = implicitly[Effect[F]].fromTry(underlying.rowsSizeM)

  def rowValuesM(index: Int): F[NebulaRecord[F]] =
    implicitly[Effect[F]].fromTry(underlying.rowValuesM(index)).map(r => new NebulaRecordImpl(r))

  def colValuesM(columnName: String): F[LazyList[ValueWrapper]] =
    implicitly[Effect[F]].fromTry(underlying.colValuesM(columnName))

  def rowsM: F[List[Row]] = implicitly[Effect[F]].fromTry(underlying.rowsM)
}

object NebulaResultSetImpl {

  final class NebulaRecordImpl[F[_]: Async](private val underlying: NebulaRecord[Try]) extends NebulaRecord[F] {

    override def iteratorM: F[Iterator[ValueWrapper]] =
      implicitly[Effect[F]].fromTry(underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): F[Unit] =
      implicitly[Effect[F]].fromTry(underlying.foreachM(f))

    override def getM(index: Int): F[ValueWrapper] = implicitly[Effect[F]].fromTry(underlying.getM(index))

    override def getM(columnName: String): F[ValueWrapper] =
      implicitly[Effect[F]].fromTry(underlying.getM(columnName))

    override def valuesM: F[LazyList[ValueWrapper]] = implicitly[Effect[F]].fromTry(underlying.valuesM)

    override def sizeM: F[Int] = implicitly[Effect[F]].fromTry(underlying.sizeM)

    override def containsM(columnName: String): F[Boolean] =
      implicitly[Effect[F]].fromTry(underlying.containsM(columnName))
  }
}
