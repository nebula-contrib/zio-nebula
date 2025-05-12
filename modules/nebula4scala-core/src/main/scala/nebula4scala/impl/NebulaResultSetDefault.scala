package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.collection.compat.immutable.LazyList
import scala.collection.convert._
import scala.concurrent.Future

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.data.value.ValueWrapper._
import nebula4scala.impl.NebulaResultSetDefault.NebulaRecordImpl
import nebula4scala.syntax._

final class NebulaResultSetDefault(underlying: ResultSet) extends NebulaResultSet[ScalaFuture] {

  def isSucceededM: ScalaFuture[Boolean] = Future(underlying.isSucceeded)

  def isEmptyM: ScalaFuture[Boolean] = Future(underlying.isEmpty)

  def errorCodeM: ScalaFuture[Int] = Future(underlying.getErrorCode)

  def spaceNameM: ScalaFuture[String] = Future(underlying.getSpaceName)

  def errorMessageM: ScalaFuture[String] = Future(underlying.getErrorMessage)

  def commentM: ScalaFuture[String] = Future(underlying.getComment)

  def latencyM: ScalaFuture[Long] = Future(underlying.getLatency)

  def planDescM: ScalaFuture[PlanDescription] = Future(underlying.getPlanDesc)

  def keysM: ScalaFuture[List[String]] = Future(underlying.getColumnNames.asScala.toList)

  def columnNamesM: ScalaFuture[List[String]] = Future(underlying.getColumnNames.asScala.toList)

  def rowsSizeM: ScalaFuture[Int] = Future(underlying.rowsSize())

  def rowValuesM(index: Int): ScalaFuture[NebulaRecord[ScalaFuture]] =
    Future(new NebulaRecordImpl(underlying.rowValues(index)))

  def colValuesM(columnName: String): ScalaFuture[LazyList[ValueWrapper]] =
    Future(underlying.colValues(columnName).asScala.map(_.asScala).to(LazyList))

  def rowsM: ScalaFuture[List[Row]] = Future(underlying.getRows.asScala.toList)

  override def toString: String = underlying.toString
}

object NebulaResultSetDefault {

  final class NebulaRecordImpl(override val underlying: Record)
      extends NebulaRecordBase(underlying)
      with NebulaRecord[ScalaFuture] {
    override def iteratorM: ScalaFuture[Iterator[ValueWrapper]] = Future(super.iterator)

    override def foreachM[U](f: ValueWrapper => U): ScalaFuture[Unit] = Future(super.iterator)

    override def valuesM: ScalaFuture[LazyList[ValueWrapper]] =
      Future(super.values)

    override def getM(index: Int): ScalaFuture[ValueWrapper] = Future(super.get(index))

    override def getM(columnName: String): ScalaFuture[ValueWrapper] = Future(super.get(columnName))

    override def sizeM: ScalaFuture[Int] = Future(super.size)

    override def containsM(columnName: String): ScalaFuture[Boolean] = Future(super.contains(columnName))
  }
}
