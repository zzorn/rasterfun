
import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
  // Scala unit testing
  val scalatest = "org.scalatest" % "scalatest" % "1.3"

  val janino = "org.codehaus.janino" % "janino" % "2.5.16"

//  def fooJars = descendents("lib" / "foo", "*.jar")
//  override def unmanagedClasspath = super.unmanagedClasspath +++ fooJars


}
