package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.collection.compat.immutable.LazyList
import scala.util.Try

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.data.value.ValueWrapper._
import nebula4scala.impl.NebulaResultSetDefault.NebulaRecordImpl
import nebula4scala.syntax._

final class NebulaResultSetDefault(underlying: ResultSet) extends NebulaResultSet[Try] {

  def isSucceededM: Try[Boolean] = Try(underlying.isSucceeded)

  def isEmptyM: Try[Boolean] = Try(underlying.isEmpty)

  def errorCodeM: Try[Int] = Try(underlying.getErrorCode)

  def spaceNameM: Try[String] = Try(underlying.getSpaceName)

  def errorMessageM: Try[String] = Try(underlying.getErrorMessage)

  def commentM: Try[String] = Try(underlying.getComment)

  def latencyM: Try[Long] = Try(underlying.getLatency)

  def planDescM: Try[PlanDescription] = Try(underlying.getPlanDesc)

  def keysM: Try[List[String]] = Try(underlying.getColumnNames.asScala.toList)

  def columnNamesM: Try[List[String]] = Try(underlying.getColumnNames.asScala.toList)

  def rowsSizeM: Try[Int] = Try(underlying.rowsSize())

  def rowValuesM(index: Int): Try[NebulaRecord[Try]] =
    Try(new NebulaRecordImpl(underlying.rowValues(index)))

  def colValuesM(columnName: String): Try[LazyList[ValueWrapper]] =
    Try(underlying.colValues(columnName).asScala.map(_.asScala).to(LazyList))

  def rowsM: Try[List[Row]] = Try(underlying.getRows.asScala.toList)

  override def toString: String = underlying.toString
}

object NebulaResultSetDefault {

  final class NebulaRecordImpl(override val underlying: Record)
      extends NebulaRecordBase(underlying)
      with NebulaRecord[Try] {
    override def iteratorM: Try[Iterator[ValueWrapper]] = Try(super.iterator)

    override def foreachM[U](f: ValueWrapper => U): Try[Unit] = Try(super.iterator)

    override def valuesM: Try[LazyList[ValueWrapper]] =
      Try(super.values)

    override def getM(index: Int): Try[ValueWrapper] = Try(super.get(index))

    override def getM(columnName: String): Try[ValueWrapper] = Try(super.get(columnName))

    override def sizeM: Try[Int] = Try(super.size)

    override def containsM(columnName: String): Try[Boolean] = Try(super.contains(columnName))
  }
}
