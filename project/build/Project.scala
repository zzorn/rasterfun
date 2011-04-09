
import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info)
{
  // Scala unit testing
  val scalatest = "org.scalatest" % "scalatest" % "1.3"

//  def fooJars = descendents("lib" / "foo", "*.jar")
//  override def unmanagedClasspath = super.unmanagedClasspath +++ fooJars


}
