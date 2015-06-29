import java.text.ParseException

import org.scalatest.FlatSpec
import vorema.{VoremaPlato, CursorPos}

/** Test if voremaplato can also see and use non-full date range
 *
 */
class SpecialTimeFormats extends FlatSpec{
   "VoremaPlato " should " parse the special timeformats" in {
      val userFileNameText = "voremaText"
      val curPos: CursorPos = CursorPos(6,38)
      val voremo = new VoremaPlato("vim", "rhythmbox", "playBackRecordings")
      info("At position "+curPos+" VoremaPlato must return the SPECIAL filename under the cursor position located in text file " + userFileNameText)
      val fileName = voremo.voremaPlatoOpen(userFileNameText, curPos)

      assert(fileName._2.nonEmpty)
      info("Filename:=" + fileName._2.get)
      info("For this test, the filename must be special (so no full date)")
      info("Is filename special? ")
      assert(try{
         info("FULL DATE: "+ voremo.FULL_TIME_FORMAT.parse(fileName._2.get))
         info("Filename was not special")
         false
      }catch{
         case e: ParseException => {
            info("Filename IS special")
            true //when not parseable (so it's a special one, return true)
         }

      })

      info("Playing first recording on the specified date (see log above)")
      val foundDates = voremo.convertToDates(fileName._1.get)
      voremo.playFile(fileName._2.get, true)
      //info("Found dates: " + voremo.convertToFilenames(foundDates));
   }

}
