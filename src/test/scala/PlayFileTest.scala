import org.scalatest.FlatSpec
import vorema.{CursorPos, VoremaPlato}


/**
 * Test case to determine if VoremaPlato can find the filename under the cursor position.
 */
class PlayFileTest extends FlatSpec {

   "VoremaPlato " should " play the filename under the cursor position" in {
      val userFileNameText = "voremaText"
      val curPos: CursorPos = CursorPos(5,26)
      val voremo = new VoremaPlato("vim", "rhythmbox", "playBackRecordings")
      info("At position "+curPos+" VoremaPlato must return the filename under the cursor position located in text file " + userFileNameText)
      val fileName = voremo.voremaPlatoOpen(userFileNameText, curPos)

      assert(fileName._2.nonEmpty)
      info("Filename:=" + fileName._2.get)
   }

}
