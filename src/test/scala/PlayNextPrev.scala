import org.scalatest.FlatSpec
import vorema.playback.{CursorPos, VoremaPlato}

/**
 * Test case to see how playNext and playPrev are working
 * Note: Only specify a cursor position with a full date, otherwise this test case doesn't work (TODO: also parse the other formats)
 */
class PlayNextPrev extends FlatSpec {
   "VoremaPlato " should " find the next and prev filename in time" in {
      val voremo = new VoremaPlato("vim", "rhythmbox", "playBackRecordings")
      info("At position 5,26 VoremaPlato must return the filename under the cursor position")
      val fileName = voremo.voremaPlatoOpen("voremaText", CursorPos(5, 26))

      assert(fileName._1.nonEmpty && fileName._2.nonEmpty) //required to have a list and a filename, otherwise this won't work

      info("seen from the current file name under the cursor")
      val allNames = fileName._1.get
      val cursorFile = fileName._2.get

      info("Is filename under cursor full date?: ")
      info("Parsed date: " + voremo.FULL_TIME_FORMAT.parse(cursorFile))

      voremo.playNextInTime(allNames, cursorFile)
      voremo.playPrev(allNames, cursorFile)
      voremo.playPrev(allNames, cursorFile)
      voremo.playNextInTime(allNames, cursorFile)
      voremo.playNextInTime(allNames, cursorFile)
      voremo.playNextInTime(allNames, cursorFile)
      voremo.playPrev(allNames, cursorFile)
      voremo.playPrev(allNames, cursorFile)
      voremo.playPrev(allNames, cursorFile)
   }
}
