/** voice_recording_play_back_tools = vorema
  */
package vorema

import java.io.File
import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date}

import scala.sys.process._

case class CursorPos(row: Int, col: Int) {
   override def toString(): String = {
      "[ROW=" + row + ", COL=" + col + "]"
   }
}

/** Log all the actions and errors from VoremaPlato
  *
  * @param theText the information text
  * @param theValue the interesting value
  */
case class Log(theText: String, theValue: Any) {
   val date_format = new SimpleDateFormat("yyyy-mm-dd-HH:MM:SS")

   def this(theText: String) {
      this(theText, "");
   }

   def printWDate() {
      val date = date_format.format(Calendar.getInstance().getTime)
      println("[[[LOG]][" + date + "] " + theText + " [[ " + theValue + " ]] ]")
   }
}

class VoremaPlato(editor: String, mediaPlayer: String, voiceRedDir: String) {
   val FULL_TIME_FORMAT = new java.text.SimpleDateFormat("'y'yyyy'_m'MM'_d'dd'_h'HH'_m'mm'_s'ss")
   val RUN_MEDIAPLAYER = true //set for testing purpose, true to actually run the mediaplayer, false to only show the command
   var CURRENT_INDEX_FILENAME = -1

   /** Used to start processing user input
     *
     * @param filename the text file input name
     * @param cursorPosition the X/Y coordinates from the cursor position
     * @return a Tuple with 1) list of all the recording filenames found in the file 2) the filename found under the given cursor position
     */
   def voremaPlatoOpen(filename: String, cursorPosition: CursorPos): (Option[List[String]], Option[String]) = {
      val fileOpen = scala.io.Source.fromFile(filename)
      val fileContents = try fileOpen.getLines().toList finally fileOpen.close()
      if (fileContents.nonEmpty) {
         val cursorLine = returnLine(fileContents, cursorPosition.row).get
         val possibleRecNames = stripRecName(cursorLine)
         val file = returnClosestRecName(cursorLine, cursorPosition.col, possibleRecNames)
         val allNames = stripAllRecNames(fileContents).filter(theList => theList != Nil).map(nex => nex.map(_._2)).flatten
         Log("Found these recording names: ", allNames)
         Log("Found filename cursor position: ", file)
         (Some(allNames), file)
      } else {
         (None, None)
      }
   }


   def findRecordingFiles(path: File): List[File] = {
      val parts = path.listFiles.toList.partition(_.isDirectory)
      parts._2 ::: parts._1.flatMap(findRecordingFiles)
   }

   def returnLine(textLines: List[String], row: Int): Option[String] = {
      textLines match {
         case head :: tail if row == 1 => Some(head)
         case head :: tail => returnLine(tail, row - 1)
         case Nil => None
      }
   }

   /** Find the closest recording filename located from the cursor position
     *
     * @param textLine The cursor located text line (row)
     * @param col The cursor located col
     * @param recnames All the possible recording name found in the text file specified by the user
     * @return A possible recording filename, closest to the cursor position
     */
   def returnClosestRecName(textLine: String, col: Int, recnames: List[(String, String)]): Option[String] = {
      val rangePosOfRec: List[(Int, Int)] = recnames.map(rec => (textLine.indexOfSlice(rec._1).toInt, textLine.indexOfSlice(rec._1) + rec._1.length.toInt))
      val ColBetween: Int = rangePosOfRec.map(range => range._1 <= col && col <= range._2).indexOf(true)
      ColBetween match {
         case x: Int if (x >= 0) => Some(recnames(ColBetween)._2)
         case _ => None
      }
   }

   /** Strip all recording names found in the text file the user submitted
     *
     * @param theText A list of text strings
     * @return A list of String tuples, one containg the clean filename, the other containing the filename including VR{ }
     */
   def stripAllRecNames(theText: List[String]): List[List[(String, String)]] = {
      theText match {
         case head :: tail => stripRecName(head) :: stripAllRecNames(tail)
         case Nil => Nil
      }
   }

   /** Strip all the recording names found in a string
     *
     * @param procContents text contents containing recording filenames
     * @return A list of tuple strings, one with a clean filename, the other one including the VR{ } part
     */
   def stripRecName(procContents: String): List[(String, String)] = {
      val recnameMatches: List[String] = ("""VR\{\|(.*?)\|\}""".r findAllIn procContents toList)
      val cleanedRec = recnameMatches.map(_.replace("VR{| ", "").replace(". |}", ""))
      recnameMatches zip cleanedRec
   }

   /** Function to play the filename recording located under the cursor positions
     *
     * @param fileName cursor located filename
     */
   def playUnderCursor(fileName: String): Unit = {
      playFile(fileName, RUN_MEDIAPLAYER)
   }

   /** Function to start playing a filename, found in the input text. This will search into the specified recording dir, and play the file in the specified music player
     *
     * @param fileName the filename that must be played
     */
   def playFile(fileName: String, runCommand: Boolean = false): Int = {
      val allFiles: List[File] = findRecordingFiles(new File(voiceRedDir))
      val findFile = allFiles.find(_.getName().contains(fileName))
      Log("Found file: ", findFile).printWDate()
      findFile match {
         case Some(file) => {
            val command = mediaPlayer + " " + file
            Log("Running command: ", command).printWDate()
            if (runCommand) {
               Process(command) !
            } else {
               1; //failure to run mediaplayer
            }
         }
         case _ => 1;
      }
   }

   /** Plays the latest filename in date before the filename located under the cursor
     *
     * @param possibleRecNames All the recording filenames in a file
     * @param fileName The recording filename located under the cursor
     */
   def playLatestBefore(possibleRecNames: List[String], fileName: String): Unit = {
      findLatestBefore(possibleRecNames, fileName) match {
         case Some(fl) => playFile(fileName, RUN_MEDIAPLAYER)
         case None => Log("Error: No previous file found before ", fileName).printWDate()
      }
   }

   /** Find the latest recording in time before the current one (located under the cursor position)
     *
     * @param possibleRecNames all the possible recording names found in the specified text file
     * @param cursorFileName the current cursor file name
     * @return possible filename found as previous filename seen from the current one
     */
   def findLatestBefore(possibleRecNames: List[String], cursorFileName: String): Option[String] = {
      val currentCursorDate = FULL_TIME_FORMAT.parse(cursorFileName)
      val theDates = convertToDates(possibleRecNames).filter(_ != currentCursorDate).sortBy(_.getTime)
      val latestBefore: Option[Date] = theDates.filter(time => currentCursorDate.after(time)) match {
         case Nil => None
         case list => Some(list.last)

      }
      val asString: Option[String] = latestBefore match {
         case Some(time) => Some(FULL_TIME_FORMAT.format(time))
         case None => None
      }
      asString
   }

   /** Convert all the filenames found in the text file specified by the user to a usable Date format
     *
     * @param possibleRecNames all the filenames found in the text file
     * @return list of Dates, None if the date was not parseable
     */
   def convertToDates(possibleRecNames: List[String]): List[Date] = {
      val theDates: List[Date] = possibleRecNames.flatMap { theTime =>
         try {
            Some(FULL_TIME_FORMAT.parse(theTime))
         }
         catch {
            case e: ParseException => None //todo, also parse the other formats..
         }
      }
      theDates
   }

   /** Plays the recording that closest before the filename under the cursor
     *
     * @param possibleRecNames All the recording filenames in a file
     * @param fileName The recording filename located under the cursor
     */
   def playEarliestBefore(possibleRecNames: List[String], fileName: String): Unit = {
      val indexOfCurrent = possibleRecNames.indexOf(fileName) //these must be ordered according to the file, from head to tail
      indexOfCurrent match {
         case -1 => Log("Filename not found in list", "").printWDate()
         case 0 => Log("No earliest before", "").printWDate()
         case _ => {
            val prevFile = possibleRecNames(indexOfCurrent - 1)
            Log("The index was: ", indexOfCurrent).printWDate()
            Log("The prev file is: ", prevFile).printWDate()
            playFile(prevFile, RUN_MEDIAPLAYER)

         }
      }
   }

   /** This will play the next filename in time seen from the filename located under the cursor.
     * Note: it is possible to call this function multiple times to 'scroll' between all the recordings
     *
     * @param possibleRecNames A list of all the recording filenames found in the specified text file
     * @param fileName A string with the filename of the recording found under the cursor position
     */
   def playNextInTime(possibleRecNames: List[String], fileName: String): Unit = {
      val theDates = convertToDates(possibleRecNames)
      val filenamesAndDates = (theDates zip theDates.map(dt => FULL_TIME_FORMAT.format(dt))).sortBy(data => data._1.getTime)
      if (CURRENT_INDEX_FILENAME < filenamesAndDates.length - 1) {
         if (CURRENT_INDEX_FILENAME == -1) {
            //start value
            CURRENT_INDEX_FILENAME = filenamesAndDates.indexOf((FULL_TIME_FORMAT.parse(fileName), fileName)) + 1
         } else {
            CURRENT_INDEX_FILENAME += 1
         }
      }
      Log("playNext: ", CURRENT_INDEX_FILENAME).printWDate()
      playFile(filenamesAndDates(CURRENT_INDEX_FILENAME)._2, RUN_MEDIAPLAYER)

   }

   /** This will play the previous filename in time seen from the filename located under the cursor.
     * Note: it is possible to call this function multiple times to 'scroll' between all the recordings
     *
     * @param possibleRecNames A list of all the found recording filenames in a text file
     * @param fileName A string with the filename of the recording found under the cursor position
     */
   def playPrev(possibleRecNames: List[String], fileName: String): Unit = {
      val theDates = convertToDates(possibleRecNames)
      val filenamesAndDates = (theDates zip theDates.map(dt => FULL_TIME_FORMAT.format(dt))).sortBy(data => data._1.getTime)
      val max = filenamesAndDates.length - 1
      if ((-1) <= CURRENT_INDEX_FILENAME && CURRENT_INDEX_FILENAME <= max) {
         if (CURRENT_INDEX_FILENAME == -1) {
            //start value
            val theIndex = filenamesAndDates.indexOf((FULL_TIME_FORMAT.parse(fileName), fileName))
            theIndex match {
               case 0 => CURRENT_INDEX_FILENAME = 0
               case _ => CURRENT_INDEX_FILENAME = theIndex - 1
            }
         } else {
            CURRENT_INDEX_FILENAME match {
               case 0 => CURRENT_INDEX_FILENAME = 0
               case _ => CURRENT_INDEX_FILENAME -= 1
            }
         }
      }

      Log("playPrev: ", CURRENT_INDEX_FILENAME).printWDate()
      playFile(filenamesAndDates(CURRENT_INDEX_FILENAME)._2, RUN_MEDIAPLAYER)
   }

}


