package nebula4scala.api

import scala.collection.JavaConverters._
import scala.collection.compat.immutable.LazyList
import scala.collection.convert._

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.data
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.data.ResultSet.Record
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.data.value._
import nebula4scala.data.value.ValueWrapper._

abstract class NebulaRecordBase(protected val underlying: Record) extends Iterable[ValueWrapper] {
  override def iterator: Iterator[ValueWrapper] = underlying.iterator().asScala.map(_.asScala)

  override def foreach[U](f: ValueWrapper => U): Unit =
    underlying.forEach((t: com.vesoft.nebula.client.graph.data.ValueWrapper) => f.apply(t.asScala))

  override def toString: String = underlying.toString

  override def size: Int = underlying.size()

  def values: LazyList[ValueWrapper] = LazyList.from(underlying.iterator().asScala.map(_.asScala))

  def get(index: Int): ValueWrapper = underlying.get(index).asScala

  def get(columnName: String): ValueWrapper = underlying.get(columnName).asScala

  def contains(columnName: String): Boolean = underlying.contains(columnName)
}

trait NebulaRecord[F[_]] {
  def iteratorM: F[Iterator[ValueWrapper]]

  def foreachM[U](f: ValueWrapper => U): F[Unit]

  def valuesM: F[LazyList[ValueWrapper]]

  def getM(index: Int): F[ValueWrapper]

  def getM(columnName: String): F[ValueWrapper]

  def sizeM: F[Int]

  def containsM(columnName: String): F[Boolean]
}

trait NebulaResultSet[F[_]] {
  def isSucceededM: F[Boolean]

  def isEmptyM: F[Boolean]

  def errorCodeM: F[Int]

  def spaceNameM: F[String]

  def errorMessageM: F[String]

  def commentM: F[String]

  def latencyM: F[Long]

  def planDescM: F[PlanDescription]

  def keysM: F[List[String]]

  def columnNamesM: F[List[String]]

  def rowsSizeM: F[Int]

  def rowValuesM(index: Int): F[NebulaRecord[F]]

  def colValuesM(columnName: String): F[LazyList[ValueWrapper]]

  def rowsM: F[List[Row]]
}
