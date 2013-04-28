== Command Examples:
* Note: You can substitue **/pd** with **/playerdata**, **/pdata** or **/playerd**.
* Note: Many of these sub commands also have aliases, for instance '/pd list|lp|pl|l' means that you can do '/pd' and then either list, lp, pl, or l in order to do that sub command.
* Note: When using the <Player> Argument, it isn't necessary to have the full username. EG: saying 'dab' will pull the last player seen who has 'dab' in their username or displayname.
* Note: All of these commands are shown as when used on my minecraft server, Newlanders.PlayerData will pull the Server Name from your server.properties for use in commands.
;/gu <PlayerName|DisplayName>
: >gu da
: First Ten Possible Auto Completes for da:
: DraculeMihawk02/#DrakyDaRPPKitty
: myamya816/#panda_cat99
: XxonethumbxX/#Legondary_Wolf
: redARCHER99/#Archi
: daboross/#Dabo
: AidanTiger24/#AidanTiger24
: KitsuK/#Commadant_K
: Dansty/#Dansty
: _dark_lover_/#RPPKittenNinja
: davidzack/#davidzack
: Total Auto Completes Found: 71

;/pd <SubCommand>
: >pd
: This is a base command, Please Use a sub command after it.
: To see all possible sub commands, type /pd ?

;/pd help|?
: >pd ?
: List Of Possible Sub Commands:
: /pd ipreverselookup/ipr/iprl <IP> Gets all different Players using an IP
: /pd loadbpd Load data from BPD PLEASE DON'T USE THIS IF YOU ARE USING XML STORAGE, IT WILL ERASE ALL XML STORAGE AND REPLACE WITH BPD DATA
: /pd bpd Save All Data AS BPD
: /pd xml Save All Data As XML
: /pd list/lp/pl/l <PageNumber> Lists all players who have ever joined this server in order of last seen
: /pd help/? This Command Views This Page
: /pd recreateall Deletes all player data and recreates it from bukkit
: /pd listfirst/lf/fl <PageNumber> List allplayers who have have ever joined this server in order of first join
: /pd viewinfo/getinfo/i <Player> Get info on a player
: /pd iplookup/ipl/ip <Player> Gets all different IPs used by a Player

;/pd viewinfo|getinfo|i <Player>
: >pd i dabo
: Info Avalible For daboross:
: Display Name: #Dabo
: daboross is not online
: daboross was last seen 5 hours, 46 minutes, and 26 seconds ago
: Times logged into Newlanders: 162
: Times logged out of Newlanders: 160
: Time Played On Newlanders: 2 days, 5 hours, 2 minutes, and 9 seconds
: First Time On Newlanders was  351 days, 5 hours, 1 minute, and 3 seconds ago
: First Time On Newlanders was  Fri May 11 17:57:16 PDT 2012
: daboross is currently Admin

;/pd list|lp|pl|l <PageNumber>
: >pd list
: Player List, Page 1:
: chasebehm was last seen Not That Long ago.
: Nickadema was last seen Not That Long ago.
: gargantaur2002 was last seen 13 minutes, and 46 seconds ago.
: CptFezchild was last seen 13 minutes, and 59 seconds ago.
: Seadrathane was last seen Not That Long ago.
: axel6565 was last seen 33 minutes, and 27 seconds ago.
: To View The Next Page, Type: /pd list 2
: \\
: >pd list 2
: Player List, Page 2:
: DraculeMihawk02 was last seen 4 minutes, and 8 seconds ago.
: DragonRider_E was last seen Not That Long ago.
: archos_omnius was last seen Not That Long ago.
: PokeMasterDJ was last seen 1 hour, 10 minutes, and 22 seconds ago.
: queenfisher6 was last seen 1 hour, 6 minutes, and 32 seconds ago.
: kingfisher8 was last seen 1 hour, 11 minutes, and 35 seconds ago.
: To View The Next Page, Type: /pd list 3

;/pd listfirst|lf|fl <PageNumber>
: >pd listfirst
: Player List, Page 1:
: Desert_Shade was first seen 351 days, 5 hours, 5 minutes, and 21 seconds ago.
: daboross was first seen 351 days, 5 hours, 5 minutes, and 21 seconds ago.
: kaimarcus was first seen 351 days, 4 hours, 20 minutes, and 57 seconds ago.
: chasebehm was first seen 351 days, 3 hours, 54 minutes, and 26 seconds ago.
: Lord_Diosk was first seen 351 days, 3 hours, 54 minutes, and 4 seconds ago.
: ballpro was first seen 351 days, 3 hours, 31 minutes, and 37 seconds ago.
: To View The Next Page, Type: /pd listfirst 2
: archos_omnius issued server command: /tpo nick
: \\
: >pd listfirst 2
: Player List, Page 2:
: Yxman was first seen 350 days, 19 hours, 32 minutes, and 25 seconds ago.
: maxilaxen123 was first seen 350 days, 18 hours, 26 minutes, and 28 seconds ago.
: Epson_Stylus was first seen 350 days, 13 hours, 50 minutes, and 46 seconds ago.
: Uknown1 was first seen 350 days, 13 hours, 13 minutes, and 2 seconds ago.
: pop2pop4pop was first seen 350 days, 11 hours, 50 minutes, and 53 seconds ago.
: meron202 was first seen 349 days, 9 hours, 52 minutes, and 41 seconds ago.
: To View The Next Page, Type: /pd listfirst 3

;/pd iplookup|ipl|ip <Player>
: >pd ip dabo
: Different IP's used by daboross
: 98.125.179.115
: 98.232.50.9
: 192.168.1.3
: 98.125.172.121
: 208.54.32.239
: 66.212.64.252

;/pd ipreverselookup|ipr|iprl <IP>
: >pd ipr 55.135.49.241
: Looking for users who have used the IP: 55.135.49.241
: Different Player's who have used the IP: 55.135.49.241
: chasebehm, CptFezchild, Overkill_Dude, xfoneguy, Lord_Diosk

* Fake IPs used in examples of /pd ip and /pd ipr
