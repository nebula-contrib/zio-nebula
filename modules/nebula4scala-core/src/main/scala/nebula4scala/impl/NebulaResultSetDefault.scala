package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.collection.compat.immutable.LazyList
import scala.collection.convert._
import scala.concurrent.Future

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.data.value.ValueWrapper._
import nebula4scala.impl.NebulaResultSetDefault.NebulaRecordImpl
import nebula4scala.syntax._

final class NebulaResultSetDefault(underlying: ResultSet) extends NebulaResultSet[SyncFuture] {

  def isSucceededM: SyncFuture[Boolean] = Future(underlying.isSucceeded)

  def isEmptyM: SyncFuture[Boolean] = Future(underlying.isEmpty)

  def errorCodeM: SyncFuture[Int] = Future(underlying.getErrorCode)

  def spaceNameM: SyncFuture[String] = Future(underlying.getSpaceName)

  def errorMessageM: SyncFuture[String] = Future(underlying.getErrorMessage)

  def commentM: SyncFuture[String] = Future(underlying.getComment)

  def latencyM: SyncFuture[Long] = Future(underlying.getLatency)

  def planDescM: SyncFuture[PlanDescription] = Future(underlying.getPlanDesc)

  def keysM: SyncFuture[List[String]] = Future(underlying.getColumnNames.asScala.toList)

  def columnNamesM: SyncFuture[List[String]] = Future(underlying.getColumnNames.asScala.toList)

  def rowsSizeM: SyncFuture[Int] = Future(underlying.rowsSize())

  def rowValuesM(index: Int): SyncFuture[NebulaRecord[SyncFuture]] =
    Future(new NebulaRecordImpl(underlying.rowValues(index)))

  def colValuesM(columnName: String): SyncFuture[LazyList[ValueWrapper]] =
    Future(underlying.colValues(columnName).asScala.map(_.asScala).to(LazyList))

  def rowsM: SyncFuture[List[Row]] = Future(underlying.getRows.asScala.toList)

  override def toString: String = underlying.toString
}

object NebulaResultSetDefault {

  final class NebulaRecordImpl(override val underlying: Record)
      extends NebulaRecordBase(underlying)
      with NebulaRecord[SyncFuture] {
    override def iteratorM: SyncFuture[Iterator[ValueWrapper]] = Future(super.iterator)

    override def foreachM[U](f: ValueWrapper => U): SyncFuture[Unit] = Future(super.iterator)

    override def valuesM: SyncFuture[LazyList[ValueWrapper]] =
      Future(super.values)

    override def getM(index: Int): SyncFuture[ValueWrapper] = Future(super.get(index))

    override def getM(columnName: String): SyncFuture[ValueWrapper] = Future(super.get(columnName))

    override def sizeM: SyncFuture[Int] = Future(super.size)

    override def containsM(columnName: String): SyncFuture[Boolean] = Future(super.contains(columnName))
  }
}
