# lobster Functions:

**Here you can find some automatic functions of this bot.**<br>
If you want to see a list of commands you can find it **[here](COMMANDS.md)**

### Bot Functions:

- [YouTube Age Limit Bypass](#youtube-age-limit)
- [Spotify API](#spotify-api)
- [Tick](#tick)
- [Voice Channel Limit](#voice-channel-limit)
- [Discord Join](#discord-join)
- [Blacklist](#blacklist)

> An explanation on what I (the dev) can see is **[here](#what-can-i-see)**

## YouTube Age Limit

There is a way to bypass the YouTube age limit, more on
that **[here](https://github.com/walkyst/lavaplayer-fork/issues/18)**<br>
This bot is using this bypass. Please make sure everyone in the voice channel is 18 or older if you play age restricted
videos.

## Spotify API

Due to Spotify's API limitations you cannot directly play songs from there.<br>
Therefore lobster searches the artist and song name on YouTube and [plays](COMMANDS.md#play-command) it from there.

## Tick

Every 60 seconds the bot is performing 2 very specific actions.

1. Update activity to "playing on x servers", where x is the amount of discord servers.
2. Check if anyone is even listening and if not, leave the voice channel. This makes sure the bot is not 24/7 in a voice
   channel and lowers the bandwidth use.

## Voice Channel Limit

If the bot joins a voice channel with a limit of how many people can be in there, it raises the allowed amount of people
by 1 during it's play session.<br>
> In case I restart the bot while it is in such a voice channel, this limit is permanently raised until you change it
> back manually. Sorry about that.

## Discord Join

When you decide to invite lobster to a discord server, it will create an entry in it's database. This allows me to save
settings permanently without using files.<br>
All that's saved is the Discord Server-ID and (currently) the prefix.

## Blacklist

This bot has a blacklist. If I notice that you're doing anything bad to the bot I will blacklist the server from using
it.<br>
If you're blacklisted you can no longer use any commands from this bot.
You can request an unban in my discord or by messaging me on discord.

## What can I see?