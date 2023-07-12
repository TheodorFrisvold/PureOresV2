# PureOresV2
Adds rare 'pure' versions of ore to the game

All sources of ore, gems or crystals can now drop a rare 'pure' version of itself. The chance can be defined individually or with a global variable.
It's possible to add more drops from blocks.

On the TODO list is adding support for entities.

## PureApi

```java
import me.favn.pureores.Pureores;
import me.favn.pureores.PureApi;

// ...

PureApi pureores = JavaPlugin.getPlugin(Pureores.class).getApi();
```

### PureApi Methods

#### `public void givePure(Player player, ItemStack item, boolean natural)`

A utility method for giving pure ore drops to players.

* **Parameters:**
   * `player` — The player to give the pure item to.
   * `item` — The item stack to give the player.
   * `natural` — Whether the item was dropped from ore, instead of given with a command.

#### `public void givePure(Player player, ItemStack item)`

A utility method for giving pure ore drops to players. This method assumes the pure item was dropped from ore, instead of given with a command.

* **Parameters:**
   * `player` — The player to give the pure item to.
   * `item` — The item stack to give the player.

#### `public Ore getPure(Material material)`

A method for getting the pure version of a base material, if it exists. If there is no pure version of the given material, this method returns `null`.

* **Parameters:**
    * `material` — The base material to get the pure version of.
* **Returns:** An `Ore` that drops the pure version of the given material, or `null`.

### Example Usage

```java
import me.favn.pureores.Pureores;
import me.favn.pureores.PureApi;
import me.favn.pureores.config.OresConfig.Ore;

// ...

// Get the PureApi instance
PureApi pureores = JavaPlugin.getPlugin(Pureores.class).getApi();
// Get the pure version of diamond
Ore diamond = pureores.getPure(Material.DIAMOND);
// Give a pure diamond to a player
if (diamond != null) {
    Player player = Bukkit.getPlayerExact("727021");
    pureores.givePure(player, diamond.toItemStack());
}
```