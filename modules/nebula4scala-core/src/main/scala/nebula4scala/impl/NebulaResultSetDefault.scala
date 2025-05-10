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

  def isSucceededM: SyncFuture[Boolean] = Future.successful(underlying.isSucceeded)

  def isEmptyM: SyncFuture[Boolean] = Future.successful(underlying.isEmpty)

  def errorCodeM: SyncFuture[Int] = Future.successful(underlying.getErrorCode)

  def spaceNameM: SyncFuture[String] = Future.successful(underlying.getSpaceName)

  def errorMessageM: SyncFuture[String] = Future.successful(underlying.getErrorMessage)

  def commentM: SyncFuture[String] = Future.successful(underlying.getComment)

  def latencyM: SyncFuture[Long] = Future.successful(underlying.getLatency)

  def planDescM: SyncFuture[PlanDescription] = Future.successful(underlying.getPlanDesc)

  def keysM: SyncFuture[List[String]] = Future.successful(underlying.getColumnNames.asScala.toList)

  def columnNamesM: SyncFuture[List[String]] = Future.successful(underlying.getColumnNames.asScala.toList)

  def rowsSizeM: SyncFuture[Int] = Future.successful(underlying.rowsSize())

  def rowValuesM(index: Int): SyncFuture[NebulaRecord[SyncFuture]] =
    Future.successful(new NebulaRecordImpl(underlying.rowValues(index)))

  def colValuesM(columnName: String): SyncFuture[LazyList[ValueWrapper]] =
    Future.successful(underlying.colValues(columnName).asScala.map(_.asScala).to(LazyList))

  def rowsM: SyncFuture[List[Row]] = Future.successful(underlying.getRows.asScala.toList)

  override def toString: String = underlying.toString
}

object NebulaResultSetDefault {

  final class NebulaRecordImpl(override val underlying: Record)
      extends NebulaRecordBase(underlying)
      with NebulaRecord[SyncFuture] {
    override def iteratorM: SyncFuture[Iterator[ValueWrapper]] = Future.successful(super.iterator)

    override def foreachM[U](f: ValueWrapper => U): SyncFuture[Unit] = Future.successful(super.iterator)

    override def valuesM: SyncFuture[LazyList[ValueWrapper]] =
      Future.successful(super.values)

    override def getM(index: Int): SyncFuture[ValueWrapper] = Future.successful(super.get(index))

    override def getM(columnName: String): SyncFuture[ValueWrapper] = Future.successful(super.get(columnName))

    override def sizeM: SyncFuture[Int] = Future.successful(super.size)

    override def containsM(columnName: String): SyncFuture[Boolean] = Future.successful(super.contains(columnName))
  }
}
