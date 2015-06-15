import org.scalatest.FlatSpec
import vorema.playback.{CursorPos, VoremaPlato}

/**
 * Test case to determine if VoremaPlato can find the filename under the cursor position.
 */
class PlayFileTest extends FlatSpec {

   "VoremaPlato " should " play the filename under the cursor position" in {
      val voremo = new VoremaPlato("vim", "rhythmbox", "playBackRecordings")
      info("At position 5,34 VoremaPlato must return the filename under the cursor position")
      val fileName = voremo.voremaPlatoOpen("voremaText", CursorPos(5, 26))

      assert(fileName._2.nonEmpty)
      info("Filename:=" + fileName._2.get)
   }

}
