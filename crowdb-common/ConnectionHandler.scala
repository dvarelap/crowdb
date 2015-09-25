package crowdb

import com.twitter.util._
import com.github.mauricio.async.db._
import com.github.mauricio.async.db.pool._
import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.util._
import java.util.concurrent.Executors
import scala.concurrent.{Future => sFuture, ExecutionContext}
import com.twitter.bijection.twitter_util._

trait ConnectionHandler[C <: Connection] {

  private[this] implicit val _ec: ExecutionContext = new TwitterExecutionContext(FuturePool(Executors.newCachedThreadPool))

  val pool: ConnectionPool[C]
  protected implicit val conn = pool

  def inTransaction[T](trans: Connection => Future[T]): Future[T] = Bijection[sFuture[T], Future[T]]{
    pool.inTransaction { conn =>
      Bijection[Future[T], sFuture[T]](trans(conn))
    }
  }

  def inSession[T](session: Connection => Future[T]): Future[T] = Bijection[sFuture[T], Future[T]]{
    pool.use { conn =>
      Bijection[Future[T], sFuture[T]](session(conn))
    }
  }
}
