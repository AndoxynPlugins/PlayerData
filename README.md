PlayerData is a plugin that gathers data on Players on **your** server!
* Records Every login time and IP
* Allows you to view the first time that each player was seen
* Allows you to view when a player was last seen
* Allows you to list every player who has ever joined the server, in order of last seen, or in order of first join.
* See what a player's Nick Name was last (If another plugin has set a custom display name on the player)
* Reverse look-up for any Nickname (Online or Offline Players) and find the Username
* Also complete a user-name from a partial username. EG: if 'daboross' and 'udaboss' had both joined your server, and you searched for 'dabo', both of them would show up.
* Has an API that allows other plugins to store user-specific data through PlayerData's storage (Also allows for other plugins to easily auto-complete a username/display name)
* Optionally find the Player's Rank, and displays that on User-Info. (Because I am looking for Ranks, currently this only works in PEX)

To install PlayerData, simply download the PlayerData.jar file and put it into your plugin folder!
If you want to grab data from Bukkit (recommended), restart your server then run the command "/pd recreateall"\\
That command will erase any current database, so only use it if you are just installing PlayerData.
It will grab a list of players who have joined your server, when they were first seen, and when they were last seen!

Java source is compiled for Java version 1.5 or 5. I think you need to have at least java 1.5 to run Bukkit, so you should be good on that account.

Go To http://dev.bukkit.org/server-mods/playerdata/ for more info!
