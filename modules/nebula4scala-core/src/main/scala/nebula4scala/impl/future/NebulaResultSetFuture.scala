package nebula4scala.impl.future

import scala.collection.compat.immutable.LazyList
import scala.concurrent.Future
import scala.util.Try

import com.vesoft.nebula.Row
import com.vesoft.nebula.graph.PlanDescription

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data.value._
import nebula4scala.impl.future.NebulaResultSetFuture.NebulaRecordImpl
import nebula4scala.impl.future.syntax._

final class NebulaResultSetFuture(underlying: NebulaResultSet[Try]) extends NebulaResultSet[Future] {

  def isSucceededM: Future[Boolean] =
    implicitly[Effect[Future]].fromTry(underlying.isSucceededM)

  def isEmptyM: Future[Boolean] = implicitly[Effect[Future]].fromTry(underlying.isEmptyM)

  def errorCodeM: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.errorCodeM)

  def spaceNameM: Future[String] = implicitly[Effect[Future]].fromTry(underlying.spaceNameM)

  def errorMessageM: Future[String] = implicitly[Effect[Future]].fromTry(underlying.errorMessageM)

  def commentM: Future[String] = implicitly[Effect[Future]].fromTry(underlying.commentM)

  def latencyM: Future[Long] = implicitly[Effect[Future]].fromTry(underlying.latencyM)

  def planDescM: Future[PlanDescription] = implicitly[Effect[Future]].fromTry(underlying.planDescM)

  def keysM: Future[List[String]] = implicitly[Effect[Future]].fromTry(underlying.keysM)

  def columnNamesM: Future[List[String]] = implicitly[Effect[Future]].fromTry(underlying.columnNamesM)

  def rowsSizeM: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.rowsSizeM)

  def rowValuesM(index: Int): Future[NebulaRecord[Future]] =
    implicitly[Effect[Future]].fromTry(underlying.rowValuesM(index)).map(f => new NebulaRecordImpl(f))

  def colValuesM(columnName: String): Future[LazyList[ValueWrapper]] =
    implicitly[Effect[Future]].fromTry(underlying.colValuesM(columnName))

  def rowsM: Future[List[Row]] =
    implicitly[Effect[Future]].fromTry(underlying.rowsM)

}

object NebulaResultSetFuture {

  final class NebulaRecordImpl(private val underlying: NebulaRecord[Try]) extends NebulaRecord[Future] {

    override def iteratorM: Future[Iterator[ValueWrapper]] =
      implicitly[Effect[Future]].fromTry(underlying.iteratorM)

    override def foreachM[U](f: ValueWrapper => U): Future[Unit] =
      implicitly[Effect[Future]].fromTry(underlying.foreachM(f))

    override def getM(index: Int): Future[ValueWrapper] = implicitly[Effect[Future]].fromTry(underlying.getM(index))

    override def getM(columnName: String): Future[ValueWrapper] =
      implicitly[Effect[Future]].fromTry(underlying.getM(columnName))

    override def valuesM: Future[LazyList[ValueWrapper]] = implicitly[Effect[Future]].fromTry(underlying.valuesM)

    override def sizeM: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.sizeM)

    override def containsM(columnName: String): Future[Boolean] =
      implicitly[Effect[Future]].fromTry(underlying.containsM(columnName))
  }
}
