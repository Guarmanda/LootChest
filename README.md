[CENTER][SIZE=7][FONT=Verdana]LootChest[/FONT][/SIZE]
[SIZE=3][FONT=Verdana]Configurable chest reloader

[IMG]https://i.imgur.com/4B01agg.gif[/IMG]
[IMG]https://i.imgur.com/R6B91lX.png[/IMG]
[/FONT][/SIZE]
[SIZE=6][B]Features:[/B][/SIZE][/CENTER]
[SIZE=4]- Set particles for each chest (35 particles supported) (can be disabled for each chest)
- BungeeCord messages on chest respawn/take
- Timer on chest's hologram (can be disabled)
- Give a chest content to a player (it allow you to create a kind of kit, since essentials kits doesn't store nbt tags)
- 1.7 to 1.15 support (No holograms in 1.7)[/SIZE]
- Menu to create or edit everything
- Editable inventory for each chest
- Editable item chance for each item in each chest (default: 100% (editable))
- Editable respawn time for each chest
- Editable hologram for each chest (can be disabled for each chest)
- Chest is deleted when empty (but will still respawn at defined time) (can be disabled)
- Editable lang file, editable menu names
- Editable general particle's speed, number, spawn rate and radius (can be disabled)
- Make a copy of another lootChest by creating a chest then selecting the chest to copy in the editing menu
- Broadcast on chest respawn (can be disabled for each chest)
- Change a chest's position
- Automatic config and lang update , so that you don't have to delete anything when you update this plugin
- Random spawn within a radius around location where chest was created, or around a random player (editable radius for each chest) (can be disabled)
- Awesome fall effect (fully editable) (can be disabled for each chests)
- Broadcast on chest taken (can be disabled for each chest)

[CENTER]You can also look this awesome video made by [URL='https://www.youtube.com/channel/UC0DKOswctz1q3gtm5BMfPtw']MusicTechnician[/URL] (English) or the one from [URL='https://www.youtube.com/channel/UCrngTubVPUjA-I2f4wuM1MQ']Maxar628 [/URL](Spanish) to know everything about this plugin:[/CENTER]
[LEFT][/LEFT]
[CENTER][MEDIA=youtube]p2Coe1gJgZY[/MEDIA][MEDIA=youtube]zX-GDKOkkvU[/MEDIA]

[SIZE=3][B][SIZE=6]Commands:[/SIZE][/B][/SIZE][/CENTER]
[SIZE=3][SIZE=4]-/lc create <name> : Creates a chest and opens creating menu[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc edit <name> : Open editing menu[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc help : Guess it[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc respawn <name> : respawn a chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc respawnall : respawn all chests[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc remove <name> : removes the given chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc setholo <name> <text> : set hologram of given chest. Setting holo to "" or " " or null will delete the holo[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc reload : reloads all chests[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc list : well... sorry to not have added that earlier[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc give <player> <chest> Allows u to give a chest's content to a player[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc setpos <name> : changes the position of a chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc settime <name> <seconds> : sets the respawn time of the chest without using the time menu[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc randomspawn <name> <radius> : sets the respawn radius of a chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc tp <name> : teleports you to a chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc togglefall <name>: enable/disable fall effect[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc getname : get name of targeted chest[/SIZE][/SIZE]
[SIZE=3][SIZE=4]-/lc locate : gives locations of all chests that haves natural respawn message enabled[/SIZE][/SIZE]
[CENTER]
[SIZE=3][B][SIZE=6]Known bugs:[/SIZE][/B][/SIZE]

[SIZE=3][SIZE=4]- Creating a chest then emptying its inventory makes it really buguy, the only way to resolve it is to delete the bugued chest. I didn't handled this bug because I thought nobody would be enough stupid to create an empty chest with a plugin that aims to make respawnable chests x)[/SIZE][/SIZE]
[SIZE=3]
- [/SIZE][SIZE=4]Holograms may not remove in some explosion or removal cases from other plugins, but I didn't experienced it since a while now[/SIZE]

[SIZE=3]
[B][SIZE=6]Permissions:[/SIZE][/B][/SIZE]
[SIZE=3][SIZE=4]for all commands: lootchest.<command>[/SIZE][/SIZE]
[SIZE=3][SIZE=4]for admins: lootchest.admin/lootchest.*[/SIZE][/SIZE]


[SIZE=3][SIZE=6][SIZE=4][B][SIZE=6]More infos:[/SIZE][/B][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=6][SIZE=4]Mail: [EMAIL]valentin@girod.fr[/EMAIL][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=6][SIZE=4]Discord: Black_Eyes#5538[/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=6][SIZE=4]github on top of page[/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=6][SIZE=4]This plugin is using InventiveTalent's [URL='https://www.spigotmc.org/resources/api-particleapi-1-7-1-8-1-9-1-10.2067/']ParticleAPI[/URL][/SIZE][/SIZE][/SIZE]
You can donate to me here [URL]https://www.paypal.com/paypalme/BlackEyes99[/URL] for all the hours I spent and will spend on this


[B][SIZE=6]Config:[/SIZE][/B][/CENTER]
[SIZE=3][code]
    #Checks for updates at plugin start
CheckForUpdates: true
    #Info messages at plugin startup
ConsoleMessages: true
    #Here are some options about particles
Particles:
      #if you disable this, no matter what you but below^^
  enable: true
  default_particle: FLAME
      #10 of these particles spawn at the same time
  number: 10
      #They respawn all 5 ticks by default
  respawn_ticks: 5
      #The radius of spawning
  radius: 0.3
      #The speed of particles. A low speed is more smooth and good-looking. A speed of 1 is just insane.
  speed: 0.05
    #time is in minutes
default_reset_time: 10
    #each item has a percentage of chance to spawn in a chest
default_item_chance: 100
    #I think you know what it is:
UseHologram: true
    #remove the chests if the player empty them (they still respawn and everything)
RemoveEmptyChests: true
    #removes the chest when a player closes it, even if there's still items in it
RemoveChestAfterFirstOpenning: false
    #Holograms doesn't act the same depending on server version, so here you can configure their height compared to the chest's height.
    #YOU CAN PUT A NEGATIVE NUMBER if the hologram is too high!
Hologram_distance_to_chest: 1
    #It was reported that placing hoppers under loot chests allows to do infinite farming, because they can respawn at same place if u want^^
PreventHopperPlacingUnderLootChest: true
    #Someone asked for this^^
Minimum_Number_Of_Players_For_Natural_Spawning: 0
    #Here comes the funny part :)
Fall_Effect:
      #the block is on an armorstand's head, so it would be 1 or 2 blocks above the chest, and not really on it
  Let_Block_Above_Chest_After_Fall: false
      #this option is for versions before 1.13: since 1.13, we have diferent material for each wool color, so we don't need this.
      #Colors are wrong, for example, PINK is CYAN and CYAN is PINK. Spigot bug, sorry.
  Optionnal_Color_If_Block_Is_Wool: CYAN
      #The block that falls. This block is on an armorstand's head
  Block: CHEST
      #The block will spawn 50 blocks above the chest then will fall to it
  Height: 50
      #You can still disable fall effect by default, and enable it for some chests only
  Enabled: true
      #Or you can let fall effect and remove fireworks
  Enable_Fireworks: true
      #0.8 is a good speed I think^^ But that's your config file x)
  Speed: 0.8


#you can edit or disable all these messages
respawn_notify:
      #should the message only appear in the world the chest is in?
  per_world_message: false
      #should a message appear when a player takes a chest?
  message_on_chest_take: true #this is a default value. You can enable or disable it for each chest
  natural_respawn:
    enabled: true #this is a default value. You can enable or disable it for each chest
    message: "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!"
  respawn_with_command:
    enabled: true #this is a default value. You can enable or disable it for each chest
    message: "&6The chest &b[Chest] &6has just respawned at [x], [y], [z]!"
  respawn_all_with_command:
    enabled: true
    message: "&6All chests where forced to respawn! Get them guys!"


[/code]
[SIZE=3]
[SIZE=3][code]
noPermission: '&6[&bLootChest&6] &cYou don''t have permission [Permission]'
notAChest: '&cyou''re not looking a chest'
chestIsEmpy: '&cThat chest is empty'
chestDeleted: '&aThe chest [Chest] &awas deleted'
chestSuccefulySaved: '&aThe chest [Chest] was succefuly created!'
chestDoesntExist: '&cThe chest [Chest] &cdoesn''t exist!'
chestAlreadyExist: '&cThe chest [Chest] &calready exists!'
succesfulyRespawnedChest: '&aThe chest [Chest] &awas respawned!'
AllChestsReloaded: '&aAll chests were respawned!'
editedParticle: '&aEdited particle of chest &b[Chest]!'
hologram_edited: '&aEdited hologram of chest &b[Chest]!'
PluginReloaded: '&aConfig file, lang, and chest data were reloaded'
ListCommand: '&aList of all chests: [List]'
copiedChest: '&6You copied the chest &b[Chest1] &6into the chest &b[Chest2]'
changedPosition: '&6You set the location of chest &b[Chest] &6to your location'
settime: '&6You successfully set the time of the chest &b[Chest]'
PlayerIsNotOnline: '&cThe player [Player] is not online'
givefrom: '&aYou were given the &b[Chest] &achest by &b[Player]'
giveto: '&aYou gave the chest &b[Chest] &ato player &b[Player]'
chestRadiusSet: '&aYou defined a spawn radius for the chest &b[Chest]'
teleportedToChest: '&aYou were teleported to chest &b[Chest]'
enabledFallEffect: '&aYou enabled fall effect for chest &b[Chest]'
disabledFallEffect: '&cYou disabled fall effect for chest &b[Chest]'
playerTookChest: '&6Oh no! &b[Player] &6found the chest &b[Chest] &6and took everything in it!'
disabledChestRadius: '&cYou disabled random spawn for chest [Chest]'
commandGetName: '&6Your''e looking the chest &b[Chest]'


Menu:
  particles:
    name: '&1Choose a chest particle!'
  copy:
    name: '&1Choose a chest to copy it'
    page: '&2---> Page &b[Number]'
  main:
    respawnTime: '&1Respawn time editing'
    content: '&1Chest content editing'
    chances: '&1Items chances editing'
    name: '&1Main editing menu'
    particles: '&1Particle choosing'
    copychest: '&1Copy settings from another chest'
    disable_fall: '&aFall effect is enabled. Click to &cDISABLE &ait'
    disable_respawn_natural: '&aNatural-respawn message is enabled. Click to &cDISABLE &ait'
    disable_respawn_cmd: '&aCommand-respawn message is enabled. Click to &cDISABLE &ait'
    disable_take_message: '&aMessage on chest take is enabled. Click to &cDISABLE &ait'
    enable_fall: '&cFall effect is disabled. Click to &aENABLE &cit'
    enable_respawn_natural: '&cNatural-respawn message is disabled. Click to &aENABLE &cit'
    enable_respawn_cmd: '&cCommand-respawn message is disabled. Click to &aENABLE &cit'
    enable_take_message:  '&cMessage on chest take is disabled. Click to &aENABLE &cit'
  chances:
    name: '&1Item chances of chest [Chest]'
    lore: '&aLeft click: +1; right: -1; shift+right: -10; shift+left: +10; tab+right: -50'
  items:
    name: '&1Items in chest [Chest]'
  time:
    infinite: '&6Deactivates the respawn time'
    name: '&1Temps de respawn'
    minutes: '&aMinutes'
    hours: '&aHours'
    days: '&aDays'
help:
  - '&a -- Help for LootChest plugin --'
  - '&a(developped by Black_Eyes, idea of Ender_Griefeur99)'
  - '&a/lc create <name> &b: Creates a chest and opens creating menu'
  - '&a/lc edit <name> &b: Open editing menu'
  - '&a/lc help &b: Guess it'
  - '&a/lc respawn <name> &b: respawn a chest'
  - '&a/lc respawnall &b: respawn all chests'
  - '&a/lc remove <name> &b: removes the given chest'
  - '&a/lc setholo <name> <text> &b: set hologram of given chest'
  - '&a/lc reload &b: reloads the plugin'
  - '&a/lc list &b: list all chests'
  - '&a/lc setpos &b: edit the position of a chest'
  - '&a/lc give <name> <player>&b: gives the chest <name> to player <player>'
  - '&a/lc settime <name> &b: sets the respawn time of a chest in seconds'
  - '&a/lc randomspawn <name> <radius> &b: make a chest respawn randomly in the specified radius (0 to disable)'
  - '&a/lc tp <name> &b: teleports you to a chest'
  - '&a/lc togglefall <name> &b: enable/disable the fall effect for a chest'
  - '&a/lc getname &b: get the name of the targeted LootChest'


[/code]

[/SIZE][/SIZE][/SIZE]

[CENTER][SIZE=3][SIZE=3][SIZE=3][SIZE=6][SIZE=4][URL='https://www.spigotmc.org/resources/api-particleapi-1-7-1-8-1-9-1-10.2067/'] [/URL][/SIZE][/SIZE][/SIZE][/SIZE][/SIZE]

[SIZE=3][SIZE=3][SIZE=3][B][SIZE=6]Screens:[/SIZE][/B][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=3][SIZE=3][SIZE=6][I][SIZE=5]All menu texts are editable[/SIZE][/I][/SIZE][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=3][SIZE=3][SIZE=6][I][SIZE=5][IMG]https://i.ibb.co/ykX2CzB/Captzaefazfeure.png[/IMG] [/SIZE][/I][/SIZE][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=3][SIZE=3][SIZE=6][I][SIZE=5][IMG]https://thumbs.gfycat.com/WeeklyBitterHerald-mobile.mp4[/IMG][/SIZE][/I][/SIZE][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=3][I][SIZE=6][SIZE=5][ATTACH=full]399747[/ATTACH][/SIZE][/SIZE][/I][/SIZE][/SIZE]
[SIZE=3][SIZE=3][SIZE=3][SIZE=6][I][SIZE=5][ATTACH=full]399748[/ATTACH][/SIZE][/I][/SIZE][/SIZE][/SIZE][/SIZE]
[SIZE=3][SIZE=3][SIZE=3][SIZE=6][I][SIZE=5][ATTACH=full]400559[/ATTACH][/SIZE][/I][/SIZE][/SIZE][/SIZE][/SIZE]

[SIZE=6][B]Using my code:[/B][/SIZE]
[SIZE=4]Don't sell copy of this thing, it will always stay open source. You can still use my code if you want to edit this plugin for yourself, or someone in needs, or you can use some function for your own plugin^^ (the fall effect class could be useful to some, and there's many menu in this, and also some config file functions) [/SIZE][/CENTER]
