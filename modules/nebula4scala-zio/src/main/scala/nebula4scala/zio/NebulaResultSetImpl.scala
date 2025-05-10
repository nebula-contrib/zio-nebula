package nebula4scala.zio

import scala.collection.compat.immutable.LazyList

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import _root_.zio._
import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.syntax._
import nebula4scala.zio.NebulaResultSetImpl.NebulaRecordImpl

final class NebulaResultSetImpl(underlying: NebulaResultSet[ScalaFuture]) extends NebulaResultSet[Task] {

  def isSucceededM: Task[Boolean] = ZIO.fromFuture(_ => underlying.isSucceededM)

  def isEmptyM: Task[Boolean] = ZIO.fromFuture(_ => underlying.isEmptyM)

  def errorCodeM: Task[Int] = ZIO.fromFuture(_ => underlying.errorCodeM)

  def spaceNameM: Task[String] = ZIO.fromFuture(_ => underlying.spaceNameM)

  def errorMessageM: Task[String] = ZIO.fromFuture(_ => underlying.errorMessageM)

  def commentM: Task[String] = ZIO.fromFuture(_ => underlying.commentM)

  def latencyM: Task[Long] = ZIO.fromFuture(_ => underlying.latencyM)

  def planDescM: Task[PlanDescription] = ZIO.fromFuture(_ => underlying.planDescM)

  def keysM: Task[List[String]] = ZIO.fromFuture(_ => underlying.keysM)

  def columnNamesM: Task[List[String]] = ZIO.fromFuture(_ => underlying.columnNamesM)

  def rowsSizeM: Task[Int] = ZIO.fromFuture(_ => underlying.rowsSizeM)

  def rowValuesM(index: Int): Task[NebulaRecord[Task]] =
    ZIO.fromFuture(_ => underlying.rowValuesM(index).map(f => new NebulaRecordImpl(f)))

  def colValuesM(columnName: String): Task[LazyList[ValueWrapper]] =
    ZIO.fromFuture(_ => underlying.colValuesM(columnName))

  def rowsM: Task[List[Row]] = ZIO.fromFuture(_ => underlying.rowsM)

}

object NebulaResultSetImpl {

  final class NebulaRecordImpl(private val underlying: NebulaRecord[ScalaFuture]) extends NebulaRecord[Task] {
    override def iteratorM: Task[Iterator[ValueWrapper]] = ZIO.fromFuture(_ => underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): Task[Unit] = ZIO.fromFuture(_ => underlying.foreachM(f))

    override def getM(index: Int): Task[ValueWrapper] = ZIO.fromFuture(_ => underlying.getM(index))

    override def getM(columnName: String): Task[ValueWrapper] = ZIO.fromFuture(_ => underlying.getM(columnName))

    override def valuesM: Task[LazyList[ValueWrapper]] = ZIO.fromFuture(_ => underlying.valuesM)

    override def sizeM: Task[Int] = ZIO.fromFuture(_ => underlying.sizeM)

    override def containsM(columnName: String): Task[Boolean] = ZIO.fromFuture(_ => underlying.containsM(columnName))
  }
}
