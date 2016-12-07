# Starship Captain
##Overview
Made during the 2016 CalHacks Hackathon at UC Berkeley, "Starship Captain" was an attempt to make a game with a unique control scheme. The game, developed with [LibGDX](https://libgdx.badlogicgames.com/), runs on Android phones and tablets and is controlled entirely through voice commands. The idea behind the game is to recreate the experience that the captain of a starship has in sci-fi movies like Star Wars and Star Trek. 

While the captain does not have direct control over any part of the ship, it is their job to issue commands to the people in charge of various systems on board. We began by brainstorming a list of systems the player should be able to command, like weapons, shields, and engines. From there we implemented the commands, created graphics, and added enemy ships with a simple AI to fight back. Due to the short time period we had to work on the game, just 36 hours, it's mostly a prototype with minimal gameplay. The speech recognition can be finicky, but when it works it's very satisfying. 

##Speech Recognition
On the bottom of the screen is a microphone button. While the player holds that button, they can state a command. The Android Speech Recognizer API is used to recognize speech and it suggests a list of strings, which are possibilities ordered by confidence. From there, each suggestion is compared to command patterns until a match is found. Regular expressions are used for the matching and are also able to extract any parameters. Ultimately the string given by the Speech Recognizer is converted to a [Command](https://github.com/wizered67/VoiceControlledShip/blob/master/core/src/com/mygdx/game/Command.java) object and then [executed](https://github.com/wizered67/VoiceControlledShip/blob/master/core/src/com/mygdx/game/Game.java#L484). The speech recognition can unfortunately still be somewhat unreliable since it is dependent entirely on the Android Speech Recognizer API correctly recognizing speech and suggesting a command as a possibility.

##Gameplay


##Commands
There are a number of different commands that have been implemented in the game. Here is the full list and their uses:
* Turn X - Turns the ship X degrees. A compass is on screen at all times so the player knows what degree corresponds to which direction.
* Fire - Fires several lasers forward, uses some energy.
* Engines - Provides a temporary speed boost, at the expense of some energy.
* Shields Up/Down - Changes the current shield status. Shields take some energy to put up initially and the ship loses energy whenever it is hit while the shields are up. However, they prevent damage to the ship's health. If energy gets too low the shields automatically deactivate.
* Scan - Uses some energy and draws a line to where the nearest enemy is at the moment. The line does not change if the enemy moves and automatically goes away after a short time, so the player may need to use the command several times to successfully track down an enemy.
* Repair - Uses a large amount of energy and restores some health.
* Missile X - Fires a missile towards X degrees. Missiles are stronger than regular lasers but more difficult to hit enemies with.

The commands were designed to be as user friendly as possible, so every command have alternative names that can be used. For example, instead of "Fire", the player can also say "Shoot" or "Attack". The commands that take a degree also can instead take a direction, such as right, left, ahead, and behind. The [regular expressions used to parse commands] (https://github.com/wizered67/VoiceControlledShip/blob/master/core/src/com/mygdx/game/Command.java#L11) were carefully designed and tested to allow as much freedom as possible and to reduce possible error. Any words can be inserted before, between, and after commands and their parameters. For example, "technicians, repair the ship!" would be considered a Repair command and "Fire all lasers towards 25 degrees" would be interpreted the same as "Fire 25". 

##Credits
* Adam Victor - design and programming
* Christine Nguyen - design and graphics
