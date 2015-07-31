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
               "\tARG4: row,col of cursor position\n")

      } else {
         val editorName = args.toList(0)
         val musicPlayer = args.toList(1)
         val voiceDir = args.toList(2)
         val filename = args.toList(3)
         val rowCol = args.toList(4)
         println("You specified the following running options: ")
         println(
            "Editor: " + editorName + "\n" +
               "musicPlayer: " + musicPlayer + "\n" +
               "voiceDir: " + voiceDir + "\n" +
               "filename: " + filename + "\n" +
               "rowCol: " + rowCol + "\n")
         editorName match{
            case "emacs" => {

               println("Todo, not yet finished. Have to find a way to convert the emacs position to a row and col position")

               val curPos: CursorPos = CursorPos(3,3) //TODO: calculate the emacs character number to a row and col position

               val voremo = new VoremaPlato("emacs", musicPlayer, voiceDir)
               val fileName = voremo.voremaPlatoOpen(filename, curPos)
               val exitValue = voremo.playFile(fileName._2.get, true)
               println("Succes? " + exitValue)
            }
            case "vim" => {
               val curPos: CursorPos = CursorPos(rowCol.split(",")(0).toInt,rowCol.split(",")(1).toInt)

               val voremo = new VoremaPlato("vim", musicPlayer, voiceDir)
               val fileName = voremo.voremaPlatoOpen(filename, curPos)
               val exitValue = voremo.playFile(fileName._2.get, true)
               println("Succes? " + exitValue)
            }
            case _ => println("You specified a non-supported editor. Please check you settings.")
         }




      }

      //voremo.playUnderCursor(fileName.getOrElse("No file found"))
   }
}


