package tech.bilal.keycloak.data

import com.typesafe.config.ConfigValueType._
import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

object Play extends App {
//  val conf = ConfigFactory.parseString("person.name: John Doe")
//  println(conf)

  implicit val personReader: ConfigReader[Person] = ConfigReader.fromCursor[Person] { cur =>
    for {
      objCur  <- cur.asObjectCursor    // 1
      nameCur <- objCur.atKey("value") // 2
//      value   = nameCur.value          //.asString     // 3
      person <- nameCur.value.valueType() match {
                 case OBJECT => ???
                 case LIST =>
                   nameCur.asList.map(_.map(_.asString)).map(x => x.foldRight())
                   ???
                 case NUMBER  => ???
                 case BOOLEAN => ???
                 case NULL    => ???
                 case STRING  => nameCur.asString.map(PersonWithName)
               }
    } yield person
  }

  val p = ConfigSource.string("person.value = [John, Doe]").load[Conf]

  println(p)
}

sealed trait Person
case class PersonWithName(value: String)       extends Person
case class PersonWithNames(value: Seq[String]) extends Person

case class Conf(person: Person)
