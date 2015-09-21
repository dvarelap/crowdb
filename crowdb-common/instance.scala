package crowdb

import com.twitter.util._
import com.github.mauricio.async.db._
import com.github.mauricio.async.db.pool._

case class DbConfig(
  futurePool: Option[FuturePool] = None,
  conf: PoolConfiguration = null,
  factory: ObjectFactory[Connection] = null
)


abstract class DbInstance(config: DbConfig) extends ConnectionHandler {

  val pool: ConnectionPool[Connection] = new ConnectionPool(config.factory, config.conf)

  protected implicit val executor: Executor = config.futurePool match {
    case None             => new CrowdbExecutor()
    case Some(futurePool) => new CrowdbExecutor(futurePool)
  }
}
