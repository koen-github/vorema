package vorema.playback{
import scala.sys.process._
case class CursorPos(row: Int, col: Int){
	override def toString(): String ={
	 "[ROW=" + row + ", COL="+col+"]"
	}
}

class VoremaPlato(editor: String, mediaPlayer: String, voiceRedDir: String){


   def voremaPlatoOpen(filename: String, cursorPosition: CursorPos): Unit ={
      val fileOpen = scala.io.Source.fromFile(filename)
      val fileContents = try fileOpen.getLines().toList finally fileOpen.close()
      if(!fileContents.isEmpty){
         val cursorLine = returnLine(fileContents, cursorPosition.row).get
         val possibleRecNames = stripRecName(cursorLine)
         val playFile = returnClosestRecName(cursorLine, cursorPosition.col, possibleRecNames)
	 
         println("Playing this file: "+ playFile + " Because cursor was located at: " + cursorPosition)
         playUnderCursor(playFile.get)
      }

   }

//HAHAHA, I just wrote the most useless recursive function ever, If you just do textLines(ROW), you get exactly the same :P
   def returnLine(textLines: List[String], row: Int): Option[String]={
      textLines match{
         case head :: tail if row == 1 => Some(head)
         case head :: tail => returnLine(tail, row - 1)
         case Nil => None
      }
   }

   def returnClosestRecName(textLine: String, col: Int, recnames: List[(String, String)]): Option[String] = {
      val rangePosOfRec: List[(Int, Int)] = recnames.map(rec => (textLine.indexOfSlice(rec._1).toInt,textLine.indexOfSlice(rec._1)+rec._1.length.toInt))
      val ColBetween: Int = rangePosOfRec.map(range => range._1 <= col && col <= range._2).indexOf(true)
      ColBetween match{
         case x: Int if(x >= 0 ) => Some(recnames(ColBetween)._2)
         case _ => None   
      }
   }

// date -d "$(echo "y2015_m04_d19_h15_m44_s49" | sed -e 's/_[h]/ /g' -e 's/[a-z]//g' -e 's/_/-/g' -e 's/-/:/3g')"


   def stripRecName(procContents: String): List[(String, String)] ={ //And yet another one here: VR{| y2015_m05_d17. |}
      val recnameMatches: List[String] = ("""VR\{\|(.*?)\|\}""".r findAllIn procContents toList)
      val cleanedRec = recnameMatches.map(_.replace("VR{| ", "").replace(". |}",""))
      recnameMatches zip cleanedRec
   }

   def playUnderCursor(fileName: String): Unit ={
	val command = mediaPlayer+" " + voiceRedDir +"/"+fileName+".mp3"
	println("Running command: " + command)
	Process(command)!
   }

   def playLatestBefore(): Unit ={

   }

   def playEarliestBefore(): Unit ={

   }

   def playNext(): Unit ={

   }

   def playPrev(): Unit ={

   }

}
}
