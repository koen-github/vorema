package vorema.playback{

object Vorema {
   def main(args: Array[String]): Unit ={
    println("Vorema - SCALA - Voice Playback tool");
    println("Usage: TOOL ");
    val voremo = new VoremaPlato("vim","rhythmbox","/home/koen")
    voremo.voremaPlatoOpen("/home/koen/vorema/voremaText",CursorPos(2,44))

   }
}
}
