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
final class NebulaResultSet(resultSet: ResultSet) {

  import NebulaResultSet._

  def isSucceeded: Boolean = resultSet.isSucceeded

  def isEmpty: Boolean = resultSet.isEmpty

  def errorCode: Int = resultSet.getErrorCode

  def spaceName: String = resultSet.getSpaceName

  def errorMessage: String = resultSet.getErrorMessage

  def comment: String = resultSet.getComment

  def latency: Long = resultSet.getLatency

  def planDesc: PlanDescription = resultSet.getPlanDesc

  def keys: List[String] = resultSet.getColumnNames.asScala.toList

  def columnNames: List[String] = resultSet.getColumnNames.asScala.toList

  def rowsSize: Int = resultSet.rowsSize()

  def rowValues(index: Int): NebulaRecord = new NebulaRecord(resultSet.rowValues(index))

  def colValues(columnName: String): List[ValueWrapper] = resultSet.colValues(columnName).asScala.toList

  def rows: List[Row] = resultSet.getRows.asScala.toList

  override def toString: String = resultSet.toString
}

object NebulaResultSet {

  final class NebulaRecord(record: Record) extends Iterable[ValueWrapper] {

    override def iterator: Iterator[ValueWrapper] = record.iterator().asScala

    override def foreach[U](f: ValueWrapper => U): Unit = record.forEach((t: ValueWrapper) => f.apply(t))

    override def toString(): String = record.toString

    def spliterator: Spliterator[ValueWrapper] = record.spliterator

    def get(index: Int): ValueWrapper = record.get(index)

    def get(columnName: String): ValueWrapper = record.get(columnName)

    def values: util.List[ValueWrapper] = record.values()

    override def size: Int = record.size()

    def contains(columnName: String): Boolean = record.contains(columnName)
  }
}
