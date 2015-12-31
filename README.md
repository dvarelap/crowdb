# crowdb

[![Build Status](https://travis-ci.org/dvarelap/crowdb.svg?branch=master)](https://travis-ci.org/dvarelap/crowdb)

Version **0.2.0-SNAPSHOT**

Simple asynchronous db access for Scala

It warps [postgresql-async](https://github.com/mauricio/postgresql-async) library

## Getting started


First let declare the model
```scala
case class Employee(firstName: String, lastName: String, position: String) extends Model
```

Then we declare the library

```scala
import io.crowdb._
import crowdb.macros._

object Library extends Instance(DbConfig.connPool) {

  import DbMacros._

  val employees    = table[Employee]

}
```

And we can use it

```scala
import Library._

val employee = Employee("Martin", "Axe", "Dev")
//createdEmployee.isNew == true


// inserts a new row for `employee` table with values ["Martin", "Axe", "Dev"]
val createdEmployee = employees.create(employee)
//createdEmployee.isNew == false
```

There are several method calls that auto generate query statements


```scala
create(m: M): Future[M]
update(m: M): Future[M]
find(id: Long): Future[Option[M]]
delete(id: Long): Future[Unit]
```
