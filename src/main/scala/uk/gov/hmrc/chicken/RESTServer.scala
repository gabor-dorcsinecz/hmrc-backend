package uk.gov.hmrc.chicken

import java.util.UUID

import scala.xml._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.duration._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString

import scala.concurrent.{Await, Future}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import twitter4j.TwitterFactory
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder

case class UserSignup(name: String, email: String, password: String)
case class UserLogin(name: String, password: String)
case class Tweet(recipient: String, message: String)
case class UserUpdate(name: String, twitterId: Option[String] = None, facebookId: Option[String] = None, whatsupId: Option[String] = None)


// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  //implicit val itemFormat = jsonFormat4(User)
  implicit val userSignupFormat = jsonFormat3(UserSignup)
  implicit val userLoginFormat = jsonFormat2(UserLogin)
  implicit val tweetFormat = jsonFormat2(Tweet)
  implicit val userUpdate = jsonFormat4(UserUpdate)
}


object RESTServer extends Directives with JsonSupport {


  def main(args: Array[String]) {

    implicit val system = ActorSystem("HoldMyRubberChicken")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher // needed for the future flatMap/onComplete in the end
    val twitter = new TwiterApi()
    val db = new Database()
    db.init()

    val route = {
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
        path("twitter") {
          post {
            entity(as[Tweet]) { tweet =>
              twitter.post(tweet.message)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "You posted: " + tweet.message))
            }
          }
        } ~
        path("dm") {
          post {
            entity(as[Tweet]) { tweet =>
              twitter.dm(tweet.recipient, tweet.message)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"You posted ${tweet.message} to ${tweet.recipient}"))
            }
          }
        } ~
        path("signup") {
          post {
            // decompress gzipped or deflated requests if required
            decodeRequest {
              // unmarshal with in-scope unmarshaller
              entity(as[UserSignup]) { signup =>
                complete {
                  println("### Signup: " + signup)
                  db.userInsert(new User(id = 0, name = signup.name, password = signup.password, email = Some(signup.email)))
                  val users = db.usersGet()
                  println("### " + users)
                  "Signed up: " + signup
                }
              }
            }
          }
        } ~
        path("login") {
          post {
            // decompress gzipped or deflated requests if required
            decodeRequest {
              // unmarshal with in-scope unmarshaller
              entity(as[UserLogin]) { login =>
                complete {
                  println("### Login: " + login)
                  val loginResult = db.login(login.name,login.password)
                  s"Logged In as ${login.name} didlogin: $loginResult "
                }
              }
            }
          }
        } ~
      path("update") {
        post {
          // decompress gzipped or deflated requests if required
          decodeRequest {
            // unmarshal with in-scope unmarshaller
            entity(as[UserUpdate]) { uu =>
              complete {
                println("### UserUpdate: " + uu)
                val loginResult = db.updateUser(uu)
                s"updating: $uu"
              }
            }
          }
        }
      }

    }


    val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 9999)

    println(s"Server online RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete{_ =>
      db.shutdown()
      system.terminate()
    } // and shutdown when done
  }


}