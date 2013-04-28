PlayerData is a plugin that gathers data on Players on **your** server!
== Feature List ==
* Records Every login time and IP
* Allows you to view the first time that each player was seen
* Allows you to view when a player was last seen
* Allows you to list every player who has ever joined the server, in order of last seen, or in order of first join.
* See what a player's Nick Name was last (If another plugin has set a custom display name on the player)
* Reverse look-up for any Nickname (Online or Offline Players) and find the Username
* Also complete a user-name from a partial username. EG: if 'daboross' and 'udaboss' had both joined your server, and you searched for 'dabo', both of them would show up.
* Has an API that allows other plugins to store user-specific data through PlayerData's storage (Also allows for other plugins to easily auto-complete a username/display name)
* Optionally find the Player's Rank, and displays that on User-Info. (Because I am looking for Ranks, currently this only works in PEX)

== Installation ==
To install PlayerData, simply download the PlayerData.jar file and put it into your plugin folder!
If you want to grab data from Bukkit (recommended), restart your server then run the command "/pd recreateall"\\
That command will erase any current database, so only use it if you are just installing PlayerData.
It will grab a list of players who have joined your server, when they were first seen, and when they were last seen!

Java source is compiled for Java version 1.5 or 5. I think you need to have at least java 1.5 to run Bukkit, so you should be good on that account.

== Commands ==
* Note: You can substitute **/pd** with **/playerdata**, **/pdata** or **/playerd**.
* Note: Many of these sub commands also have aliases, for instance '/pd list|lp|pl|l' means that you can do '/pd' and then either list, lp, pl, or l in order to do that sub command.
* Note: When using the <Player> Argument, it isn't necessary to have the full username. EG: saying 'dab' will pull the last player seen who has 'dab' in their username or displayname.

;/gu <PlayerName|DisplayName>
: This command returns up to 10 players who have the User Name/Display Name given within their username|displayName.
: The players are in order of last seen.

;/pd <SubCommand>
: Main command. Use one of the sub-commands below.

;/pd list|lp|pl|l <PageNumber>
: Lists all players who have ever joined this server in order of last seen, in pages.

;/pd help|?
: This command views an in-game list of commands and command help.

;/pd recreateall
: Deletes all data gathered and recreates it from bukkit (This will pull last login and player list from Bukkit's storage of offline players)
: There is NO way to recover data deleted with this command as it is auto-saved into the XML database.

;/pd listfirst/lf/fl <PageNumber>
: Lists all players who have have ever joined this server in order of first join.

;/pd viewinfo/getinfo/i <Player>
: Get info on a player (First Seen, Last Seen, Time Played, Nick Name, ...).

;/pd iplookup/ipl/ip <Player>
: Gets all different IPs used by <Player>.

;/pd ipreverselookup|ipr|iprl <IP>
: Gets a list of all players who have ever logged in with the IP <IP>

;/pd xml
: Save all data into the XML backend. PlayerData auto-saves into the XML Database, and does a total-save when it is unloaded, so this command usually isn't needed.

;--/pd loadbpd--
: Load data from BPD Storage Format! This command is kept for migrating BPD databases into XML, pretty much ALL of the people getting this from Bukkit will NEVER want to use this.

;--/pd bpd--
: Save all data into the BPD backend! This command was created because some people might find it helpful? It will be removed in the future.

== Permissions ==

;playerdata.*:
: Gives access to all PlayerData commands
: children:
: playerdata.help: true
: playerdata.viewinfo: true
: playerdata.list: true
: playerdata.set: true
: playerdata.admin: true
: default: false

;playerdata.help:
: Allows you to use /pd (Required for all sub commands)
: defaults to true

;playerdata.viewinfo:
: Allows you to use /pd i
: defaults to true
: children:
: playerdata.help: true

;playerdata.list:
: Allows you to use /pd list
: defaults to true
: children:
: playerdata.help: true

;playerdata.admin:
: PlayerData Admin. This command allows for use of commands that can erase the whole database. It is recomended to not give this permission to anyone, and only allow this from console.
: defaults to false
: children:
: playerdata.help: true

;playerdata.firstjoinlist:
: Allows you to list Player Data in order of first join
: defaults to true
: children:
: playerdata.help: true

;playerdata.iplookup:
: Allows you to look up what IPs a player has used
: defaults to false
: children:
: playerdata.help: true

;playerdata.ipreverselookup:
: Allows you to look up all Players who have used an IP
: defaults to false
: children:
: playerdata.help: true

== Source ==
PlayerData's source is avaible on GitHub: [[https://github.com/daboross/PlayerData|https://github.com/daboross/PlayerData]]

== TODO ==
* Add Command Examples to this page.
* Allow for looking through login/logout logs
* Command to see the last IP a player used
* Add mode JavaDoc to the source of PlayerData
== Problems or Comments? ==
* Report Problems and Submit future Ideas at the Github Repo: https://github.com/daboross/PlayerData/issues
* Submit an issue there, and I will try to get to it, whether it be a question or a bug in my code.
* Also I will try to read comments posted below, but please don't post bugs in the comments section.
* If you have an addition that you would like me to add, and you are willing to code it, it is a lot more likely to get done.
* Fork the github project, then send a pull request!
