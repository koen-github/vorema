

class VoremaPlato(editor: String, mediaPlayer: String, voiceRedDir: String){
   case class CursorPos(row: Int, col: Int){
      override def toString(): String ={
         "[ROW=" + row + ", COL="+col+"]"
      }
   }

   def voremaplato(filename: String, cursorPosition: CursorPos): Unit ={
      val fileOpen = scala.io.Source.fromFile(filename)
      val fileContents = try fileOpen.getLines().toList finally fileOpen.close()
      if(!fileContents.isEmpty){
         val cursorLine = returnLine(fileContents, cursorPosition.row)

         println("REGEX FOUND: "+ stripRecName(cursorLine))
      }

   }

   def returnLine(textLines: List[String], row: Int): String={
      textLines match{
         case head :: tail if row == 1 => head
         case head :: tail => returnLine(tail, row - 1)
         case Nil => "[NOSUCHLINE]"
      }
   }

// date -d "$(echo "y2015_m04_d19_h15_m44_s49" | sed -e 's/_[h]/ /g' -e 's/[a-z]//g' -e 's/_/-/g' -e 's/-/:/3g')"


   def stripRecName(procContents: String): Option[String] ={ //And yet another one here: VR{| y2015_m05_d17. |}
      """(?s)VR\{\|(.*)\|\}""".r findFirstIn procContents
      // echo "This is elucidated in more detail in VR{| y2015_m05_d17_h15_m16_s34. |} However, this contradicts what is said in VR{| y2015_m05_d14_o002. |}." | grep -Po '(?<=VR\{\|)(.*)(?=\|\})'
   }

   def playUnderCursor(): Unit ={

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