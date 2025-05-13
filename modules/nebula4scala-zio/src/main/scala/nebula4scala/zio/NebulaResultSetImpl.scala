package nebula4scala.zio

import scala.collection.compat.immutable.LazyList
import scala.util.Try

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import _root_.zio._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.syntax._
import nebula4scala.zio.NebulaResultSetImpl.NebulaRecordImpl
import nebula4scala.zio.syntax._

final class NebulaResultSetImpl(underlying: NebulaResultSet[Try]) extends NebulaResultSet[Task] {

  def isSucceededM: Task[Boolean] =
    implicitly[Effect[Task]].fromTry(underlying.isSucceededM)

  def isEmptyM: Task[Boolean] = implicitly[Effect[Task]].fromTry(underlying.isEmptyM)

  def errorCodeM: Task[Int] = implicitly[Effect[Task]].fromTry(underlying.errorCodeM)

  def spaceNameM: Task[String] = implicitly[Effect[Task]].fromTry(underlying.spaceNameM)

  def errorMessageM: Task[String] = implicitly[Effect[Task]].fromTry(underlying.errorMessageM)

  def commentM: Task[String] = implicitly[Effect[Task]].fromTry(underlying.commentM)

  def latencyM: Task[Long] = implicitly[Effect[Task]].fromTry(underlying.latencyM)

  def planDescM: Task[PlanDescription] = implicitly[Effect[Task]].fromTry(underlying.planDescM)

  def keysM: Task[List[String]] = implicitly[Effect[Task]].fromTry(underlying.keysM)

  def columnNamesM: Task[List[String]] = implicitly[Effect[Task]].fromTry(underlying.columnNamesM)

  def rowsSizeM: Task[Int] = implicitly[Effect[Task]].fromTry(underlying.rowsSizeM)

  def rowValuesM(index: Int): Task[NebulaRecord[Task]] =
    implicitly[Effect[Task]].fromTry(underlying.rowValuesM(index)).map(f => new NebulaRecordImpl(f))

  def colValuesM(columnName: String): Task[LazyList[ValueWrapper]] =
    implicitly[Effect[Task]].fromTry(underlying.colValuesM(columnName))

  def rowsM: Task[List[Row]] =
    implicitly[Effect[Task]].fromTry(underlying.rowsM)

}

object NebulaResultSetImpl {

  final class NebulaRecordImpl(private val underlying: NebulaRecord[Try]) extends NebulaRecord[Task] {

    override def iteratorM: Task[Iterator[ValueWrapper]] =
      implicitly[Effect[Task]].fromTry(underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): Task[Unit] =
      implicitly[Effect[Task]].fromTry(underlying.foreachM(f))

    override def getM(index: Int): Task[ValueWrapper] = implicitly[Effect[Task]].fromTry(underlying.getM(index))

    override def getM(columnName: String): Task[ValueWrapper] =
      implicitly[Effect[Task]].fromTry(underlying.getM(columnName))

    override def valuesM: Task[LazyList[ValueWrapper]] = implicitly[Effect[Task]].fromTry(underlying.valuesM)

    override def sizeM: Task[Int] = implicitly[Effect[Task]].fromTry(underlying.sizeM)

    override def containsM(columnName: String): Task[Boolean] =
      implicitly[Effect[Task]].fromTry(underlying.containsM(columnName))
  }
}
