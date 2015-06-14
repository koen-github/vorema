import org.scalatest.FlatSpec
import vorema.playback.{CursorPos, VoremaPlato}

class PlayFileTest extends FlatSpec {

   "VoremaPlato " should " play the filename under the cursor position" in {
      val voremo = new VoremaPlato("vim", "rhythmbox", "playBackRecordings")
      info("At position 3,43 VoremaPlato must return the filename under the cursor position")
      val fileName = voremo.voremaPlatoOpen("voremaText", CursorPos(2, 126))
      assert(fileName.nonEmpty)
      info("Filename:=" + fileName.get)


   }



}
