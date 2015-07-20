#!/bin/bash
##script to install vorema to a specific location, and edit the emacs or gvim plugin##
CURRENT_LOCATION=`pwd`
JAR_LOCATION=`find $CURRENT_LOCATION -name "Vorema-assembly-1.0.jar"`
mkdir -p $CURRENT_LOCATION/user_specific



echo "###################################################################"
echo "#################### INSTALL SCRIPT FOR VOREMA ####################"
echo "###################################################################"

echo "FOUND ASSEMBLY JAR: $JAR_LOCATION"

echo "Re-compile voremo jar?: "
options=("yes" "no" )
select opt in "${options[@]}"
do
    case $opt in
        "yes")
	    echo "###################################################################"
	    echo "###################### RECOMPILING VOREMA JAR #####################"
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

echo "Select editor to use with vorema: "

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
	    
		VIM_PLUGIN_LOCATION="$CURRENT_LOCATION/editorPlugins/(g)vim"
	    PLUGIN_CONTENTS=`tail -n +7 "$VIM_PLUGIN_LOCATION/vim_plugin_vorema"`
	    PLUGIN_OPTIONS="
function! Vorema_plackback()
let editorName = \"vim\"
let musicplayer = \"$MUSIC_PLAYER\"
let jarVoremoLocation = \"$JAR_LOCATION\"
let voiceRedDir = \"$VOICE_DIR\"
		"
FULL_PLUGIN_CONTENTS=$PLUGIN_OPTIONS$PLUGIN_CONTENTS
	    VIM_NEW_PLUGIN="$CURRENT_LOCATION/user_specific/generated_vim_plugin.vim"	
	    echo "$FULL_PLUGIN_CONTENTS" > $VIM_NEW_PLUGIN
		
 	    echo "###################################################################"
	    echo "COPY NEW VIM PLUGIN TO ~/.vim/plugin/? (y/n), followed by [ENTER]:"
	    read VIM_FILE_OPTION

	    if [ "$VIM_FILE_OPTION" = "y" ]; then
		mkdir -p $HOME/.vim/plugin
		echo "Copying generated vim plugin to home vim directory"
		cp $VIM_NEW_PLUGIN $HOME/.vim/plugin
	    fi
	    echo "Done installing as (g)vim plugin, use F4 in a text to use this script"
	    exit 0;
            ;;
        "Quit")
            break
	    exit 1;
            ;;
        *) echo invalid option;;
    esac
done
