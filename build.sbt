name := "Vorema"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.txt")

scalacOptions in (Compile, doc) ++= Seq("-doc-title", "VoremaPlato")

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

test in assembly := {}



    
