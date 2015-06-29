package vorema

object Vorema {
   def main(args: Array[String]): Unit = {
      println("Vorema - SCALA - Voice Playback tool");
      if (args.length != 5) {
         println("Usage: vorema [ARG0...ARG5]");
         println("Vorema can play audio files located under the cursor in an editor.\nIf the name of the recording is formatted in a special way")
         println("Note: you must specify every arg or this program won't run\n\n")
         println(
            "\tARG0: the name of the editor\n" +
               "\tARG1: the name of your musicplayer\n" +
               "\tARG2: directory of voice recordings\n" +
               "\tARG3: filename of current text file\n" +
               "\tARG4: row of cursor position\n" +
               "\tARG5: col of cursor posotion\n")

      } else {
         println("You specified the following running options: ")
         println(
            "ARG0: " + args.toList(0) + "\n" +
               "ARG1: " + args.toList(1) + "\n" +
               "ARG2: " + args.toList(2) + "\n" +
               "ARG3: " + args.toList(3) + "\n" +
               "ARG4: " + args.toList(4) + "\n")
      }

      //voremo.playUnderCursor(fileName.getOrElse("No file found"))
   }
}


