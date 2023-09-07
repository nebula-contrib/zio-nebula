package zio.nebula

import java.util
import java.util.Spliterator

import scala.jdk.CollectionConverters._

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data._
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
final class NebulaResultSet(underlying: ResultSet) {

  import NebulaResultSet._

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

  def colValues(columnName: String): List[ValueWrapper] = underlying.colValues(columnName).asScala.toList

  def rows: List[Row] = underlying.getRows.asScala.toList

  override def toString: String = underlying.toString
}

object NebulaResultSet {

  final class NebulaRecord(private val underlying: Record) extends Iterable[ValueWrapper] {

    override def iterator: Iterator[ValueWrapper] = underlying.iterator().asScala

    override def foreach[U](f: ValueWrapper => U): Unit = underlying.forEach((t: ValueWrapper) => f.apply(t))

    override def toString(): String = underlying.toString

    def spliterator: Spliterator[ValueWrapper] = underlying.spliterator

    def get(index: Int): ValueWrapper = underlying.get(index)

    def get(columnName: String): ValueWrapper = underlying.get(columnName)

    def values: util.List[ValueWrapper] = underlying.values()

    override def size: Int = underlying.size()

    def contains(columnName: String): Boolean = underlying.contains(columnName)
  }
}
