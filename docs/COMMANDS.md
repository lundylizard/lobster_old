# lobster Commands:

Here you can find a list of available commands of this bot.<br>
You can find other functions of this bot **[here](FUNCTIONS.md)**

### Music Commands:

- [Play](#play-command)
- [Move](#move-command)
- [Remove](#remove-command)
- [Skip](#skip-command)
- [Seek](#seek-command)
- [Shuffle](#shuffle-command)
- [Leave](#leave-command)
- [Queue](#queue-command)
- [Join](#join-command)
- [Stop](#stop-command)
- [NowPlaying](#nowplaying-command)

### Miscellaneous Commands:

- [Prefix](#prefix-command)
- [Invite](#invite-command)

## Play Command

**The play command is used to add a song to the music queue.**
<br>
If lobster is not in a voice channel already, it will [join](#join-command) it automatically.
<br>
<br>
**Aliases:** ``p``, ``sr``
<br>
**Usage:** ``play <top> [search term | url]``
<br>
<br>
**Arguments:**<br>
``top`` - **Optional:** Adds the requested song to the top of the queue.<br>
``search term`` - Search term used for YouTube search.<br>
``url`` - If an url is provided, it will try to play the song associated with that website.
<br>
<br>
**Example:** ``play top devil town cavetown``
<br>
This will search for the video "devil town cavetown" on YouTube and put it to the top of the queue.
<br>
<br>
**Example:** ``play https://open.spotify.com/track/0vf2eBw2inhl8y61cYQMv2``
<br>
This will play a song from Spotify.
<br>
**Please note:** Due to [Spotify's limitations](FUNCTIONS.md#spotify-api) this will search the song on YouTube instead.
<br>
<br>
**Supported & Tested Websites:**

- YouTube
- Spotify
- SoundCloud

**You can also request embedded files or links to audio files.**
<br>
<br>
<img src="images/embedded_file.png" alt="Embedded file playback"/>
<br>
<br>
**This uses the song's metadata:**
<br>
<img src="images/embedded_file_info.png">

## Move Command

**The move command is used to move songs in the queue.**
<br>
**Aliases:** ``mv``
<br>
**Usage:** ``move [from] [to]``<br>

**Arguments:**<br>
``from`` - Song which will be moved.<br>
``to`` - Position the song will move to.
<br>
<br>
**Example:** ``move 6 9``
<br>
This will move the song at position 6 to position 9.

## Remove Command

**The remove command is used to remove songs from the queue.**
<br>
<br>
**Aliases:** ``rm``
<br>
**Usage:** ``remove [index | range]``<br>

**Arguments:**<br>
``index`` - Position of the song to remove.<br>
``range`` - A range of positions to be removed.
<br>
<br>
**Example:** ``remove 6``
<br>
This will remove the song at position 6 from the queue.
<br>
<br>
**Example:** ``remove 3-7``
<br>
This will remove all songs from position 3 to 7.

## Skip Command

**The skip command is used to skip songs (in the queue).**
<br>
<br>
**Aliases:** ``s``
<br>
**Usage:** ``skip <amount>``<br>

**Arguments:**<br>
``amount`` - **Optional:** Amount of songs to be skipped.
<br>
<br>
**Example:** ``skip 2``
<br>
This will skip 2 songs.

## Seek Command

**The seek command is used to jump to a time in the song.**
<br>
<br>
**Aliases:** None
<br>
**Usage:** ``seek <time>``<br>

**Arguments:**<br>
``time`` - Time to jump to.
<br>
<br>
**Example:** ``seek 2:34``
<br>
This will jump to position 02:34 of the current song.

## Shuffle Command

**The shuffle command is used randomize the song queue.**
<br>
**Aliases:** None
<br>
**Usage:** ``shuffle <seed>``<br>

**Arguments:**<br>
``seed`` - **Optional:** Set Seed for randomization.

## Leave Command

**The leave command is used to make the bot leave the voice channel.**
<br>
This will also stop the playback and clear the queue.
<br>
<br>
**Aliases:** ``disconnect``, ``dc``
<br>
**Usage:** ``leave``<br>

**Arguments:**<br>
None.

## Queue Command

**The queue command will send you a list of the upcoming songs.**
<br>
It will only show the first 10 songs.
<br>
<br>
**Aliases:** ``q``
<br>
**Usage:** ``queue``<br>

**Arguments:**<br>
None.

## Join Command

**The join command makes the bot join the voice channel.**
<br>
<br>
**Aliases:** None
<br>
**Usage:** ``join``<br>

**Arguments:**<br>
None.

## Stop Command

**The stop command will make the bot leave the voice channel and clear the queue.**
<br>
<br>
**Aliases:** None.
<br>
**Usage:** ``stop``<br>

**Arguments:**<br>
None.

## NowPlaying Command

**The nowplaying command will show you what song is currently playing**
<br>
<br>
**Aliases:** ``np``
<br>
**Usage:** ``nowplaying``<br>

**Arguments:**<br>
None.

## Prefix Command

**The prefix command allows server admins to change the symbol used before commands.**<br>
If no argument is provided, it will show you the current prefix.
<br>
<br>
**Aliases:** None.
<br>
**Usage:** ``prefix <prefix>``<br>

**Arguments:**<br>
``prefix`` - New prefix used for commands (Max Length: 10)
<br>
<br>
**Example:** ``prefix --``<br>
This will change the command prefix to ``--``. Future commands will now require to be used with this prefix: ``--queue``
, ``--leave``

## Invite Command

**The invite command will send a link to invite the bot to your server.**
<br>
<br>
**Aliases:** None.
<br>
**Usage:** ``invite``

**Arguments:**<br>
None.
