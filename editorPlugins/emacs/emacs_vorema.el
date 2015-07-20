;UNCOMMENT AND CHANGE FOR MANUAL INSTALL, OTHERWISE USE SCRIPT
;(setq editorName "emacs")
;(setq musicPlayer "mplayer")
;(setq jarVoremoLocation "/home/koen/vorema/target/scala-2.11/Vorema-assembly-1.0.jar")
;(setq voiceRedDir "/home/koen/vorema/playBackRecordings")


(defun run-voremo ()
  "Run voremo with the settings "
  (interactive)
  (save-restriction
    (widen)
    (save-excursion
	(setq charLocation (point))
	(setq file (buffer-file-name))
	(setq cmd-string (concat "java -jar " jarVoremoLocation " " editorName " " musicPlayer " " voiceRedDir  " " file " " (number-to-string charLocation)  " "))
	;(message "The command: %s" cmd-string)
	(shell-command cmd-string "*RUNNING VOREMO*" ))))


(global-set-key (kbd "C-c c") 'run-voremo)
