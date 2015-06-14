/** voice_recording_play_back_tools = vorema
  */
package vorema.playback

import java.text.{SimpleDateFormat, ParseException}
import java.util.{Calendar, Date}

import scala.sys.process._

case class CursorPos(row: Int, col: Int) {
   override def toString(): String = {
      "[ROW=" + row + ", COL=" + col + "]"
   }
}

case class Log(theText: String, theValue: Any){
   val date_format = new SimpleDateFormat("yyyy-mm-dd-HH:MM:SS")
   def this(theText: String) {
      this(theText, "");
   }

   def printWDate() {
      val date = date_format.format(Calendar.getInstance().getTime)
      println("[[[LOG]][" + date+"] " + theText+" [[ "+theValue+" ]] ]")
   }
}

class VoremaPlato(editor: String, mediaPlayer: String, voiceRedDir: String) {
   val FULL_TIME_FORMAT = new java.text.SimpleDateFormat("'y'yyyy'_m'MM'_d'dd'_h'HH'_m'mm'_s'ss")


   def voremaPlatoOpen(filename: String, cursorPosition: CursorPos): Option[String] = {
      val fileOpen = scala.io.Source.fromFile(filename)
      val fileContents = try fileOpen.getLines().toList finally fileOpen.close()
      if (fileContents.nonEmpty) {
         val cursorLine = returnLine(fileContents, cursorPosition.row).get
         val possibleRecNames = stripRecName(cursorLine)
         val file = returnClosestRecName(cursorLine, cursorPosition.col, possibleRecNames)
         val allNames = stripAllRecNames(fileContents).filter(theList => theList != Nil).map(nex => nex.map(_._2)).flatten
         println("All recnames in file: "+ allNames)
         playEarliestBefore(allNames, file.get)
         file
      } else{
         None
      }
   }

   //hahahaa, I just wrote the most useless recursive function ever, If you just do textLines(ROW), you get exactly the same :P
   def returnLine(textLines: List[String], row: Int): Option[String] = {
      textLines match {
         case head :: tail if row == 1 => Some(head)
         case head :: tail => returnLine(tail, row - 1)
         case Nil => None
      }
   }

   def returnClosestRecName(textLine: String, col: Int, recnames: List[(String, String)]): Option[String] = {
      val rangePosOfRec: List[(Int, Int)] = recnames.map(rec => (textLine.indexOfSlice(rec._1).toInt, textLine.indexOfSlice(rec._1) + rec._1.length.toInt))
      val ColBetween: Int = rangePosOfRec.map(range => range._1 <= col && col <= range._2).indexOf(true)
      ColBetween match {
         case x: Int if (x >= 0) => Some(recnames(ColBetween)._2)
         case _ => None
      }
   }

   def stripAllRecNames(theText: List[String]): List[List[(String, String)]] ={
      theText match {
         case head :: tail => stripRecName(head) :: stripAllRecNames(tail)
         case Nil => Nil
      }
   }

   // date -d "$(echo "y2015_m04_d19_h15_m44_s49" | sed -e 's/_[h]/ /g' -e 's/[a-z]//g' -e 's/_/-/g' -e 's/-/:/3g')"


   def stripRecName(procContents: String): List[(String, String)] = {
      //And yet another one here: VR{| y2015_m05_d17. |}
      val recnameMatches: List[String] = ("""VR\{\|(.*?)\|\}""".r findAllIn procContents toList)
      val cleanedRec = recnameMatches.map(_.replace("VR{| ", "").replace(". |}", ""))
      recnameMatches zip cleanedRec
   }

   def convertToDates(possibleRecNames: List[String]): List[Date] = {
      val theDates: List[Date] = possibleRecNames.flatMap{ theTime =>
         try {
            Some(FULL_TIME_FORMAT.parse(theTime))
         }
         catch{
            case e: ParseException => None //todo, also parse the other formats..
         }
      }
      theDates
   }

   def findLatestBefore(possibleRecNames: List[String], cursorFileName: String): Option[String] = {
      val currentCursorDate = FULL_TIME_FORMAT.parse(cursorFileName)
      val theDates = convertToDates(possibleRecNames).filter(_ != currentCursorDate).sortBy(_.getTime)
      val latestBefore: Option[Date] = theDates.filter(time => currentCursorDate.after(time)) match{
         case Nil => None
         case list => Some(list.last)

      }
      val asString: Option[String] = latestBefore match{
         case Some(time) => Some(FULL_TIME_FORMAT.format(time))
         case None => None
      }
      asString
   }

   /** Function to start playing a filename, found in the input text. This will search into the specified recording dir, and play the file in the specified music player
     *
     * @param fileName the filename that must be played
     */
   def playFile(fileName:String): Unit = {
      val command = mediaPlayer + " " + voiceRedDir + "/" + fileName + ".mp3" //todo, search for file instead of adding .mp3
      Log("Running command: ", command).printWDate()
      Process(command) !
   }

   /** Function to play the filename recording located under the cursor positions
    *
    * @param fileName cursor located filename
    */
   def playUnderCursor(fileName: String): Unit = {
      playFile(fileName)
   }

   /** Plays the latest filename in date before the filename located under the cursor
    *
     * @param possibleRecNames All the recording filenames in a file
     * @param fileName The recording filename located under the cursor
    */
   def playLatestBefore(possibleRecNames: List[String], fileName: String): Unit = {
      findLatestBefore(possibleRecNames, fileName) match{
         case Some(fl) => playFile(fileName)
         case None => Log("Error: No previous file found before ",fileName).printWDate()
      }
   }

   /** Plays the recording that closest before the filename under the cursor
    *
    * @param possibleRecNames All the recording filenames in a file
    * @param fileName The recording filename located under the cursor
    */
   def playEarliestBefore(possibleRecNames: List[String], fileName: String): Unit = {
      val indexOfCurrent = possibleRecNames.indexOf(fileName) //these must be ordered according to the file, from head to tail
      indexOfCurrent match{
         case -1 => Log("Filename not found in list", "").printWDate()
         case 0 => Log("No earliest before", "").printWDate()
         case _ => {
            val prevFile = possibleRecNames(indexOfCurrent-1)
            Log("The index was: ", indexOfCurrent).printWDate()
            Log("The prev file is: ",  prevFile).printWDate()
            playFile(prevFile)

         }
      }
   }

   def playNext(): Unit = {

   }

   def playPrev(): Unit = {

   }

}


