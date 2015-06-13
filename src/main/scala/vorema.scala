package vorema.playback

object Vorema {
   def main(args: Array[String]): Unit = {
      println("Vorema - SCALA - Voice Playback tool");
      println("Usage: TOOL TODO-write usage ");
      val voremo = new VoremaPlato("vim", "rhythmbox", "/home/koen/playbacks")
      val fileName = voremo.voremaPlatoOpen("/home/koen/vorema/voremaText", CursorPos(3, 43))
      //voremo.playUnderCursor(fileName.getOrElse("No file found"))
   }
}


