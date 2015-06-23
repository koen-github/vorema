import org.scalatest.FlatSpec
import vorema.{VoremaPlato, CursorPos}

/** Determine if the recording filename found in the text at the user submitted cursor location is able to play in the specified musicplayer
 *
 */
class MediaplayerOpening extends FlatSpec {
   "VoremaPlato " should " should open and play a mediaplayer" in {
      val userFileNameText = "voremaText"
      val mediaPlayer = "rhythmbox"
      val curPos: CursorPos = CursorPos(5,26)
      val voremo = new VoremaPlato("vim", mediaPlayer, "playBackRecordings")
      val fileName = voremo.voremaPlatoOpen(userFileNameText, curPos)

      info("At position "+curPos+" VoremaPlato must return the filename under the cursor position located in text file " + userFileNameText)
      assert(fileName._2.nonEmpty)

      info("VoremaPlato should open a mediaplayer, and start playing file: ")
      info("Filename:=" + fileName._2.get)

      info("Open mediaplayer: " + mediaPlayer)
      val exitValue = voremo.playFile(fileName._2.get, true)
      info("Exit value must be zero: " + exitValue)
      assert(exitValue == 0);

   }

}
