package nebula4scala.zio

import scala.collection.compat.immutable.LazyList

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import _root_.zio._
import nebula4scala._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.value._
import nebula4scala.syntax._
import nebula4scala.zio.NebulaResultSetImpl.NebulaRecordImpl

final class NebulaResultSetImpl(underlying: NebulaResultSet[SyncFuture]) extends NebulaResultSet[Task] {

  def isSucceededM: Task[Boolean] = ZIO.fromFuture(ec => underlying.isSucceededM)

  def isEmptyM: Task[Boolean] = ZIO.fromFuture(ec => underlying.isEmptyM)

  def errorCodeM: Task[Int] = ZIO.fromFuture(ec => underlying.errorCodeM)

  def spaceNameM: Task[String] = ZIO.fromFuture(ec => underlying.spaceNameM)

  def errorMessageM: Task[String] = ZIO.fromFuture(ec => underlying.errorMessageM)

  def commentM: Task[String] = ZIO.fromFuture(ec => underlying.commentM)

  def latencyM: Task[Long] = ZIO.fromFuture(ec => underlying.latencyM)

  def planDescM: Task[PlanDescription] = ZIO.fromFuture(ec => underlying.planDescM)

  def keysM: Task[List[String]] = ZIO.fromFuture(ec => underlying.keysM)

  def columnNamesM: Task[List[String]] = ZIO.fromFuture(ec => underlying.columnNamesM)

  def rowsSizeM: Task[Int] = ZIO.fromFuture(ec => underlying.rowsSizeM)

  def rowValuesM(index: Int): Task[NebulaRecord[Task]] =
    ZIO.fromFuture(implicit c => underlying.rowValuesM(index).map(f => new NebulaRecordImpl(f)))

  def colValuesM(columnName: String): Task[LazyList[ValueWrapper]] =
    ZIO.fromFuture(ec => underlying.colValuesM(columnName))

  def rowsM: Task[List[Row]] = ZIO.fromFuture(ec => underlying.rowsM)

}

object NebulaResultSetImpl {

  final class NebulaRecordImpl(private val underlying: NebulaRecord[SyncFuture]) extends NebulaRecord[Task] {
    override def iteratorM: Task[Iterator[ValueWrapper]] = ZIO.fromFuture(ec => underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): Task[Unit] = ZIO.fromFuture(ec => underlying.foreachM(f))

    override def getM(index: Int): Task[ValueWrapper] = ZIO.fromFuture(ec => underlying.getM(index))

    override def getM(columnName: String): Task[ValueWrapper] = ZIO.fromFuture(ec => underlying.getM(columnName))

    override def valuesM: Task[LazyList[ValueWrapper]] = ZIO.fromFuture(ec => underlying.valuesM)

    override def sizeM: Task[Int] = ZIO.fromFuture(ec => underlying.sizeM)

    override def containsM(columnName: String): Task[Boolean] = ZIO.fromFuture(ec => underlying.containsM(columnName))
  }
}
