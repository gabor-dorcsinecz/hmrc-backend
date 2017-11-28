package uk.gov.hmrc.chicken

import scalikejdbc._


case class User(id: Long, name: String, password: String, email: Option[String], twitterId: Option[String] = None, facebookId: Option[String] = None, whatsupId: Option[String] = None)

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"

  def apply(rs: WrappedResultSet) = new User(
    rs.long("id"),
    rs.string("name"),
    rs.string("password"),
    rs.stringOpt("email"),
    rs.stringOpt("twitterId"),
    rs.stringOpt("facebookId"),
    rs.stringOpt("whatsupId")
    //rs.jodaDateTime("created_at")
  )
}

class Database {
  implicit val session = AutoSession

  def init() {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:file:./HMRChicken", "user", "pass")
    //ConnectionPool.singleton("jdbc:h2:mem:HMRChicken", "user", "pass")
    // ad-hoc session provider on the REPL

    // table creation, you can run DDL by using #execute as same as JDBC
    sql"""
  create table users(
  id serial not null primary key,
  name varchar(64),
  email varchar(64),
  password varchar(64),
  twitterId varchar(64),
  facebookId varchar(64),
  whatsupId varchar(64)
  )
  """.execute.apply()
  }

  def shutdown(): Unit = {
    ConnectionPool.closeAll()
  }

  def usersGet(): List[User] = {
    sql"select * from users".map(rs => User(rs)).list().apply()
  }

  def userInsert(user: User): Unit = {
    sql"insert into users (name, password, email) values (${user.name}, ${user.password}, ${user.email.getOrElse("")})".update.apply()
    //sql"insert into users (name, password, email, twitterId, facebookId, whatsupId) values (${user.name}, ${user.password}, ${user.email})".update.apply
  }

  def login(username:String,password:String):Boolean = {
    val user = sql"select * from users where name = ${username}".map(rs => User(rs)).single().apply()
    user.map(u => u.password == password).getOrElse(false)
  }

  def updateUser(user:UserUpdate): Unit = {
    sql"UPDATE users SET twitterId = ${user.twitterId.getOrElse("")}, facebookId = ${user.facebookId.getOrElse("")}, whatsupId = ${user.whatsupId.getOrElse("")} WHERE name = ${user.name}".update.apply
  }


}

