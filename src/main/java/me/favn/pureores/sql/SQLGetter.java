package me.favn.pureores.sql;

import me.favn.pureores.Pureores;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private Pureores plugin;
    public SQLGetter(Pureores plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement ps;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS placedblocks " + "(NAME VARCHAR(100),UUID VARCHAR(100),POINTS INT(100),PRIMARY KEY (NAME))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM placedblocks WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            results.next();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO placedblocks" + " (NAME,UUID) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean exists(UUID uuid){
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM placedblocks WHERE UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPoints(UUID uuid, int points) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE placedblocks SET POINTS=? WHERE UUID=?");
            ps.setInt(1, (getPoints(uuid) + points));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT POINTS FROM placedblocks WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int points = 0;
            if (rs.next()){
                points = rs.getInt("POINTS");
                return points;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // DELETE STUFF

    public void emptyTable() {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("TRUNCATE placedblocks");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("DELETE FROM placedblocks WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
