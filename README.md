***
# Description
### Music bot for discord. Has some weird features.
***
# Features (commands):
### handling music:
* **/clearQueue** - clears the queue;
* **/delete _position:_** *[position]* - deletes track from the queue with given position;
  * **/do _action:_** *[action]* **_condition:_** *[condition]* - does some action when condition is met;
    * Actions:
      * **_join-to:_** *[@nickname]* - joins to someone if he in voice chat;
      * **_play:_** *url* - plays music with given url;
    * Conditions:
      * **_when-user-in-vc:_** *[@nickname]* - does action when someone with given nickname joins voice chat;
      * **_when-time-is_:** *[hh:mm]* - does action when current time will equal with given time;
* **/now playing** - returns info of playing track. If nothing is playing return error message;
* **/play _url_:** *url* - plays track with given url. Can be used without parameters;
* **/playlist _add:_** *url* - adds given url to playlists file;
* **/play next:** *url* - adds track with given url very next;
* **/queue** - returns list of songs from the queue;
* **/repeat: _times_** *[number]* - repeats the track for given times. Can be used without parameters for infinity repeating;
* **/resume** - resumes paused track;
* **/shuffle** - shuffles the queue;
* **/skip** - skips current track and plays next from the queue;
* **/stop** - stops playing, clears the queue;
  ***/volume _number:_** *[number]* - sets volume level to given number. Can be used without parameters to see what level is;
### handling voice chat:
* **/join** - joins in voice chat which user in;
* **/leave** - leaves voice chat;
### handling connection:
* **restart** - restarts bot;
* **shutdown** - turns off bot;
***