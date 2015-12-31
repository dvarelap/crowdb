package io.crowdb


import com.github.mauricio.async.db._
import com.twitter.bijection.Bijection
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.util._
import java.util.concurrent.Executors
import scala.concurrent.{Future => sFuture, ExecutionContext}

trait Executor {

  import com.twitter.bijection.twitter_util._
  private[this] implicit val _ec: ExecutionContext = new TwitterExecutionContext(futurePool)

  def futurePool: FuturePool

  def exec[R](statement: Statement, convert: ResultSet => R)(implicit conn: Connection): Future[R] = Bijection[sFuture[R], Future[R]] {
    conn.sendPreparedStatement(statement.sql, statement.values).map { qr =>
      qr.rows match {
        case None     => throw new DatabaseException(s" error while executing query ${statement.sql} with params " + s"[${statement.values}] no results returned")
        case Some(rs) => convert(rs)
      }
    }
  }
}

class CrowdbExecutor(val futurePool: FuturePool = FuturePool(Executors.newCachedThreadPool)) extends Executor
