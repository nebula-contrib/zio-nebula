package nebula4scala.data

import java.util.stream.StreamSupport

import scala.jdk.CollectionConverters.*
import scala.jdk.StreamConverters.*

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.data.NebulaResultSet.*
import nebula4scala.data.value.*
import nebula4scala.data.value.ValueWrapper.*

final class NebulaResultSet(underlying: ResultSet) {

  def isSucceeded: Boolean = underlying.isSucceeded

  def isEmpty: Boolean = underlying.isEmpty

  def errorCode: Int = underlying.getErrorCode

  def spaceName: String = underlying.getSpaceName

  def errorMessage: String = underlying.getErrorMessage

  def comment: String = underlying.getComment

  def latency: Long = underlying.getLatency

  def planDesc: PlanDescription = underlying.getPlanDesc

  def keys: List[String] = underlying.getColumnNames.asScala.toList

  def columnNames: List[String] = underlying.getColumnNames.asScala.toList

  def rowsSize: Int = underlying.rowsSize()

  def rowValues(index: Int): NebulaRecord = new NebulaRecord(underlying.rowValues(index))

  def colValues(columnName: String): List[ValueWrapper] = underlying.colValues(columnName).asScala.map(_.asScala).toList

  def rows: List[Row] = underlying.getRows.asScala.toList

  override def toString: String = underlying.toString
}

object NebulaResultSet {

  final class NebulaRecord(private val underlying: Record) extends Iterable[ValueWrapper] {

    override def iterator: Iterator[ValueWrapper] = underlying.iterator().asScala.map(_.asScala)

    override def foreach[U](f: ValueWrapper => U): Unit =
      underlying.forEach((t: com.vesoft.nebula.client.graph.data.ValueWrapper) => f.apply(t.asScala))

    override def toString(): String = underlying.toString

    def lazyValues(parallel: Boolean = false): LazyList[ValueWrapper] =
      StreamSupport.stream(underlying.spliterator(), parallel).map(_.asScala).toScala(LazyList)

    def get(index: Int): ValueWrapper = underlying.get(index).asScala

    def get(columnName: String): ValueWrapper = underlying.get(columnName).asScala

    def values: List[ValueWrapper] = underlying.values().asScala.map(_.asScala).toList

    override def size: Int = underlying.size()

    def contains(columnName: String): Boolean = underlying.contains(columnName)
  }
}
