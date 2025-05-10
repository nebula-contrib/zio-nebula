package nebula4scala.api

import nebula4scala.data.NebulaResultSet

trait NebulaSessionClient[F[_]] {

  /** Execute the nGql sentence.
   *
   *  @param stmt
   *    The nGql sentence. such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
   *  @return
   */
  def execute(stmt: String): F[NebulaResultSet]

  /** close the session pool
   */
  def close(): F[Unit]

  /** if the SessionPool has been initialized
   */
  def isActive: F[Boolean]

  /** if the SessionPool is closed
   */
  def isClosed: F[Boolean]

  /** get the number of all Session
   */
  def sessionNum: F[Int]

  /** get the number of idle Session
   */
  def idleSessionNum: F[Int]
}
