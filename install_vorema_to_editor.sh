#!/bin/bash
##script to install vorema to a specific location, and edit the emacs or gvim plugin##
CURRENT_LOCATION=`pwd`
JAR_LOCATION=`find $CURRENT_LOCATION -name "Vorema-assembly-1.0.jar"`
mkdir -p $CURRENT_LOCATION/user_specific



echo "###################################################################"
echo "#################### INSTALL SCRIPT FOR VOREMO ####################"
echo "###################################################################"

echo "FOUND ASSEMBLY JAR: $JAR_LOCATION"

echo "Re-compile voremo jar?: "
options=("yes" "no" )
select opt in "${options[@]}"
do
    case $opt in
        "yes")
	    echo "###################################################################"
	    echo "###################### RECOMPILING VOREMO JAR #####################"
	    echo "###################################################################"
            cd `pwd`
	    sbt assembly
            break
	    ;;
        "no")
            break
            ;;
        *) echo invalid option;;
    esac
done

echo "###################################################################"
echo "Type your favorite music player, followed by [ENTER]:"

read MUSIC_PLAYER

echo "###################################################################"

echo "Specify full path to your voice recordings, followed by [ENTER]:"

read VOICE_DIR

echo "###################################################################"

echo "Select editor to use with voremo: "

options=("emacs" "(g)vim" "Quit")
select opt in "${options[@]}"
do
    case $opt in
        "emacs")
            echo "you chose emacs as editor"
	    EMACS_PLUGIN_LOCATION="$CURRENT_LOCATION/editorPlugins/emacs"
	    PLUGIN_CONTENTS=`tail -n +7 "$EMACS_PLUGIN_LOCATION/emacs_vorema.el"`
	    PLUGIN_OPTIONS="
	    (setq editorName \"emacs\")
	    (setq musicPlayer \"$MUSIC_PLAYER\")
	    (setq jarVoremoLocation \"$JAR_LOCATION\")
	    (setq voiceRedDir \"$VOICE_DIR\")
		"

	    FULL_PLUGIN_CONTENTS=$PLUGIN_OPTIONS$PLUGIN_CONTENTS
	    EMACS_NEW_PLUGIN="$CURRENT_LOCATION/user_specific/generated_emacs_plugin.el"	
	    echo "$FULL_PLUGIN_CONTENTS" > $EMACS_NEW_PLUGIN
	
 	    echo "###################################################################"
	    echo "ADD AND CHANGE .EMACS FILE? (y/n), followed by [ENTER]:"

	    read EMACS_FILE_OPTION
	    EMACS_CONTENTS="(add-to-list 'load-path \"$CURRENT_LOCATION/user_specific/\")(load \"generated_emacs_plugin.el\")"
	    if [ "$EMACS_FILE_OPTION" = "y" ]; then
		echo "Changing emacs file, location: $HOME/.emacs"
		echo "$EMACS_CONTENTS" >> $HOME/.emacs
	    fi

	    echo "Done installing, use script in emacs with Ctrl+c c"
	    exit 0;

            ;;
        "(g)vim")
            echo "you chose (g)vim as editor"
            ;;
        "Quit")
            break
	    exit 1;
            ;;
        *) echo invalid option;;
    esac
done
