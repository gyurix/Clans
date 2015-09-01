This plugin can be used for clan management.

[center][color=red][size=18pt][b]Critical dependency:[/b][/size][/color][/center]
[size=14pt][b]Please download and install [url=http://www.project-rainbow.org/site/downloads/?sa=view;down=201]Api-Collection[/url] plugin. If you didn't do it, this plugin won't work.
[/b][/size]


[center][b][size=18pt]Commands and permissions:[/size][/b][/center]

[b]Permission for every command:[/b]
[b]clans.use[/b] and [b]clans.command.<commandname>[/b]

[b]/c[/b]
only [b]clans.use[/b] permission
Lists all the clan management commands.

[u][b]/c new <name>[/b][/u]
Creates clan

[u][b]/c remove [name][/b][/u]
[b]Remove[/b] in clan permission.
[b]clans.removeanyclan[/b] permission for using the optional name argument
Removes your or the given clan.

[u][b]/c invite [playername][/b][/u]
[b]Invite[/b] in clan permission.
Invites a player to your clan or lists the current invitation list if the playername isn't given.

[u][b]/c accept[/b][/u]
Accepts the clan invitation

[u][b]/c deny[/b][/u]
Denies the clan invitation

[u][b]/c ally [clanname] [-][/b][/u]
[b]Ally[/b] in clan permission only if you give any arguments.
Using it without arguments writes your clans allys, pending ally requests and requested allys.
Giving the clan name has several functions:
If you aren't ally with the given clan name, then it will send an allyrequest for the given clan.
If you already sent this allyrequest, then it will cancel it.
If you got an allyrequest for the given clan, then it accepts the request.
If you are already ally with this clan, then it will break this ally.
The - parameter is only for refusing the incoming ally request.

[u][b]/c prefix [newclanprefix][/b][/u]
[b]Prefix[/b] in clan permission only for modifying your clans prefix.
Changes your clans prefix, or writes the current prefix, if the newclanprefix parameter isn't given

[u][b]/c suffix [newclansuffix][/b][/u]
[b]Suffix[/b] in clan permission only for modifying your clans suffix.
Changes your clans suffix, or writes the current suffix, if the newclansuffix parameter isn't given

[u][b]/c displayname [newdisplayname][/b][/u]
[b]Name[/b] in clan permission only for modifying your clans displayname.
Changes your clans displayname, or writes the current displayname, if the newclandisplayname parameter isn't given

[u][b]/c chat[/b][/u]
Toggles clan chat

[u][b]/c leave[/b][/u]
Use it if you want to leave your current clan. Be carefull, defaultly you can't leave your clan, if you are the only clan owner.

[u][b]/c kick <player> [reason][/b][/u]
[b]Kick[/b] in clan permission
Kicks the given player for the given reason, defaultly you can't kick the clan owner, if he is owner alone.

[u][b]/c editranks [rank] [permission1[permission2[permission...]]][/b][/u]
[b]EditRanks[/b] in clan permission
With this command you can manage the in-clan rank system.
If you use it without any argument, you will get a rank-permissions list.
Giving the first argument will cause rank creating, if the given rank isn't exists yet, or removing, if the given rank already exists.
Giving the second argument will cause flipping the given permissions in the given rank. If the given rank doesn't exists, it will create it with the given permissions.
All the players of any deleted rank are moved to the member rank. You can't modify owner ranks permissions, and can't delete owner and member rank.

[u][b]/c rank [player] [rank][/b][/u]
[b]Rank[/b] in clan permission if you gave any parameters.
It writes your rank and permissions, if you use it without any argument.
If you give the player argument, then the given players rank and it's permissions will be written,
If you give the rank argument, then you will change the given players rank. 
Defaultly clan needs at least one owner, so the last owners rank can't be changed.

[u][b]/c spawn[/b][/u]
Teleports you to the clan spawn

[u][b]/c setspawn[/b][/u]
[b]SetSpawn[/b] in clan permission
Sets clan spawn point to the command executer players actuall location.

[u][b]/c list[/b][/u]
Lists all the clans

[u][b]/c info [clan][/b][/u]
[b]clans.infoanyclan[/b] for using the clan argument
Writes the following information about your or the given clan: name, prefix, suffix, ranks and its players

[u][b]/c maxplayers [newmaxplayernumber][/b][/u]
[b]MaxPlayer[/b] in clan permission only for setting new player limit.
Without argument it writes the current player number and the maximal player number of your clan. If you give the newmaxplayernumber argument, then it changes your clans maxplayernumber onto the given value.

[u][b]/c save[/b][/u]
Saves all the clans.

[u][b]/c reload[/b][/u]
Reloads all the clans and the language file.

[center][b][size=18pt]Configuration:[/size][/b][/center]
Clans are stored in [b]clans.yml[/b] file, which you don't need to edit, but you can, if you want.
The language file, [b]lang.lng[/b] is in the jar file, it's already translated to German thanks to CloudeLecaw.
The configuration is the [b]config.yml[/b] file, where each thing is commented, so you will know, what do you want to change.

[center][b][size=18pt]Changelog:[/size][/b][/center]
[b][size=14pt]1.1.0[/size][/b]
- Added clan displaynames, which you can see and change using [b]/c dn[/b] command
- Added tab-complete support for every command
- Moved language file, lang.lng to the plugins directory
- Added version and forcelangcopy to configuration
- Some minor other fixes

[center][b][size=18pt][url=http://www.project-rainbow.org/site/plugin-releases/clans/new/#new]Forum[/url][/size][/b][/center]