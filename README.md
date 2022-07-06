<dl class="customResourceFieldnative_mc_version">
<dt>Native Minecraft Version:</dt>
<dd>1.19</dd>
</dl>
<dl class="customResourceFieldmc_versions">
<dt>Tested Minecraft Versions:</dt>
<dd><ul class="plainList"><li>1.7</li><li>1.8</li><li>1.9</li><li>1.10</li><li>1.11</li><li>1.12</li><li>1.13</li><li>1.14</li><li>1.15</li><li>1.16</li><li>1.17</li><li>1.18</li></ul></dd>
</dl>
<dl class="customResourceFieldsource_code">
<dt>Source Code:</dt>
<dd><a href="https://github.com/Guarmanda/LootChest" rel="nofollow" class="externalLink" target="_blank">https://github.com/Guarmanda/LootChest</a></dd>
</dl>
<dl class="customResourceFieldcontributors">
<dt>Contributors:</dt>
<dd>Black_Eyes</dd>
</dl>
<dl class="customResourceFieldlanguages">
<dt>Languages Supported:</dt>
<dd>Editable language file</dd>
</dl>
<dl class="customResourceFielddonate_link">
<dt>Donation Link:</dt>
<dd><a href="https://www.paypal.me/BlackEyes99" rel="nofollow" class="externalLink" target="_blank">https://www.paypal.me/BlackEyes99</a></dd>
</dl>
</div>
<div style="text-align: center"><span style="font-size: 26px"><span style="font-family: 'Verdana'">LootChest</span></span><br />
<span style="font-size: 12px"><span style="font-family: 'Verdana'">Configurable chest reloader<br />
<br />

</span></span><br />
<span style="font-size: 22px"><b>Features:</b></span>&#8203;</div><span style="font-size: 15px">- Set particles for each chest (35 particles supported) (can be disabled for each chest)<br />
- BungeeCord messages on chest respawn/take<br />
- Timer on chest&#039;s hologram (can be disabled)<br />
- Give a chest content to a player (it allow you to create a kind of kit, since essentials kits doesn&#039;t store nbt tags)<br />
- 1.7 to 1.19 support (No holograms in 1.7)</span><br />
- Menu to create or edit everything<br />
- Editable inventory for each chest<br />
- Editable item chance for each item in each chest (default: 100% (editable))<br />
- Editable respawn time for each chest<br />
- Editable hologram for each chest (can be disabled for each chest)<br />
- Chest is deleted when empty (but will still respawn at defined time) (can be disabled)<br />
- Editable lang file, editable menu names<br />
- Editable general particle&#039;s speed, number, spawn rate and radius (can be disabled)<br />
- Make a copy of another lootChest by creating a chest then selecting the chest to copy in the editing menu<br />
- Broadcast on chest respawn (can be disabled for each chest)<br />
- Change a chest&#039;s position<br />
- Automatic config and lang update , so that you don&#039;t have to delete anything when you update this plugin<br />
- Random spawn within a radius around location where chest was created, or around a random player (editable radius for each chest) (can be disabled)<br />
- Awesome fall effect (fully editable) (can be disabled for each chests)<br />
- Broadcast on chest taken (can be disabled for each chest)<br />
<br />
<div style="text-align: center">You can also look this awesome video made by <a href="https://www.youtube.com/channel/UC0DKOswctz1q3gtm5BMfPtw" target="_blank" class="externalLink" rel="nofollow">MusicTechnician</a> (English) or the one from <a href="https://www.youtube.com/channel/UCrngTubVPUjA-I2f4wuM1MQ" target="_blank" class="externalLink" rel="nofollow">Maxar628 </a>(Spanish) to know everything about this plugin:&#8203;</div><div style="text-align: left">&#8203;</div><div style="text-align: center">
<br />
<span style="font-size: 12px"><b><span style="font-size: 22px">Commands:</span></b></span>&#8203;</div><span style="font-size: 12px"><span style="font-size: 15px">-/lc create &lt;name&gt; : Creates a chest and opens creating menu</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc edit &lt;name&gt; : Open editing menu</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc help : Guess it</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc respawn &lt;name&gt; : respawn a chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc respawnall : respawn all chests</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc remove &lt;name&gt; : removes the given chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc setholo &lt;name&gt; &lt;text&gt; : set hologram of given chest. Setting holo to &quot;&quot; or &quot; &quot; or null will delete the holo</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc reload : reloads all chests</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc list : well... sorry to not have added that earlier</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc give &lt;player&gt; &lt;chest&gt; Allows u to give a chest&#039;s content to a player</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc setpos &lt;name&gt; : changes the position of a chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc settime &lt;name&gt; &lt;seconds&gt; : sets the respawn time of the chest without using the time menu</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc randomspawn &lt;name&gt; &lt;radius&gt; : sets the respawn radius of a chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc tp &lt;name&gt; : teleports you to a chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc togglefall &lt;name&gt;: enable/disable fall effect</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc getname : get name of targeted chest</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">-/lc locate : gives locations of all chests that haves natural respawn message enabled</span></span><br />
<div style="text-align: center"><br />
<span style="font-size: 12px"><b><span style="font-size: 22px">Known bugs:</span></b></span><br />
<br />
<span style="font-size: 12px"><span style="font-size: 15px">- Creating a chest then emptying its inventory makes it really buguy, the only way to resolve it is to delete the bugued chest. I didn&#039;t handled this bug because I thought nobody would be enough stupid to create an empty chest with a plugin that aims to make respawnable chests x)</span></span><br />
<span style="font-size: 12px"><br />
- </span><span style="font-size: 15px">Holograms may not remove in some explosion or removal cases from other plugins, but I didn&#039;t experienced it since a while now</span><br />
<br />
<span style="font-size: 12px"><br />
<b><span style="font-size: 22px">Permissions:</span></b></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">for all commands: lootchest.&lt;command&gt;</span></span><br />
<span style="font-size: 12px"><span style="font-size: 15px">for admins: lootchest.admin/lootchest.*</span></span><br />
<br />
<br />
<span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px"><b><span style="font-size: 22px">More infos:</span></b></span></span></span><br />
<span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px">Mail: <a href="/cdn-cgi/l/email-protection#3147505d545f45585f715658435e551f5743"><span class="__cf_email__" data-cfemail="93e5f2fff6fde7fafdd3f4fae1fcf7bdf5e1">valentin@girod.fr</span></a></span></span></span><br />
<span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px">Discord: Black_Eyes#5538</span></span></span><br />
<span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px">github on top of page</span></span></span><br />
<span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px">This plugin is using InventiveTalent&#039;s <a href="https://www.spigotmc.org/resources/api-particleapi-1-7-1-8-1-9-1-10.2067/" class="internalLink">ParticleAPI</a></span></span></span><br />
You can donate to me here <a href="https://www.paypal.com/paypalme/BlackEyes99" target="_blank" class="externalLink" rel="nofollow">https://www.paypal.com/paypalme/BlackEyes99</a> for all the hours I spent and will spend on this<br />

<br />
<div style="text-align: center"><span style="font-size: 12px"><span style="font-size: 12px"><span style="font-size: 12px"><span style="font-size: 22px"><span style="font-size: 15px"><a href="https://www.spigotmc.org/resources/api-particleapi-1-7-1-8-1-9-1-10.2067/" class="internalLink"> </a></span></span></span></span></span><br />
<br />

<span style="font-size: 22px"><b>Using my code:</b></span><br />
<span style="font-size: 15px">Don&#039;t sell copy of this thing, it will always stay open source. You can still use my code if you want to edit this plugin for yourself, or someone in needs, or you can use some function for your own plugin^^ (the fall effect class could be useful to some, and there&#039;s many menu in this, and also some config file functions) </span>&#8203;</div>
</blockquote></article>


