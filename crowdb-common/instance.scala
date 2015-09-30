package crowdb

import com.twitter.util._
import com.github.mauricio.async.db._
import com.github.mauricio.async.db.pool._
import java.util.concurrent.Executors

abstract class Instance[C <: Connection](val pool: ConnectionPool[C]) extends ConnectionHandler[C] {

  private[this] val _futurePool: FuturePool = FuturePool(Executors.newCachedThreadPool)
  protected def futurePool     : FuturePool = _futurePool

  implicit val executor: Executor = new CrowdbExecutor(futurePool)

  // needed to implement crietria
  implicit def toOp[T](colName: String): Operator[T] = Operator[T](colName)
}
