package com.minecave.gangs.gang;

import com.minecave.gangs.storage.Messages;
import com.minecave.gangs.storage.MsgVar;
import com.minecave.gangs.util.LimitedQueue;
import com.minecave.gangs.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Created by Carter on 5/25/2015.
 */
public class Hoodlum {

    @Getter
    private final UUID playerUUID;
    @Setter
    private volatile int power;
    @Getter
    @Setter
    private int maxPower;
    @Getter
    @Setter
    private Gang gang;
    @Getter
    @Setter
    private boolean pledged;
    @Getter
    @Setter
    private UUID gangUUID;
    @Getter
    @Setter
    private GangRole role;
    @Getter
    @Setter
    private LocalDateTime lastOnline;
    @Getter
    @Setter
    private LocalDateTime lastOffline;
    @Getter
    private List<String> invites;
    @Getter
    @Setter
    private boolean autoClaim;
    private LimitedQueue<String> notices;

    public Hoodlum(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.power = 10;
        this.maxPower = 10;
        updateLastTimes();
        this.autoClaim = false;
        this.invites = new ArrayList<>();
        this.role = GangRole.GANGLESS;
        this.notices = new LimitedQueue<>(5);
    }

    public void addPower(int amount) {
        addPower(amount, false);
    }

    public synchronized void addPower(int amount, boolean force) {
        power = (power + amount > maxPower ? (force ? amount + power : maxPower) : amount + power);
    }

    public void removePower(int amount) {
        removePower(amount, false);
    }

    public synchronized void removePower(int amount, boolean force) {
        this.power = (power - amount < -maxPower ? (force ? power - amount : maxPower) : power - amount);
    }

    public synchronized int getPower() {
        return power;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public boolean isInGang() {
        return gang != null;
    }

    public boolean hasRole(GangRole role){
        if(role.equals(GangRole.SERVER_ADMIN)){
            return this.getPlayer().hasPermission("gangs.admin");
        }else{
            return this.role.ordinal() >= role.ordinal();
        }
    }

    public int getMinPower() {
        return -maxPower;
    }

    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(player.isOnline())
            player.sendMessage(message);
    }

    public void updateLastTimes() {
        setLastOnline(LocalDateTime.now());
        if(role != GangRole.GANGLESS && gang != null) {
            gang.setLastOnline(LocalDateTime.now());
        }
        setLastOffline(LocalDateTime.now());
    }

    public boolean hasInvite(String gangName){
        return invites.contains(gangName.toLowerCase());
    }

    public boolean isOnline() {
        Player player = getPlayer();
        return player != null && player.isOnline();
    }

    public void addNotice(String string) {
        LocalDate date = LocalDate.now();
        String timestamp = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + date.getDayOfMonth();
        this.notices.add(timestamp + ": " + StringUtil.colorString(string));
    }

    public List<String> getWelcomer(){
        List<String> messages = new ArrayList<>();
        messages.add(Messages.get("welcome.header", MsgVar.PLAYER.var(), getPlayer().getName()));
        if(isInGang()){
            messages.addAll(getGang().getMessageBoard());
        }
        messages.add("");
        messages.addAll(notices);
        return StringUtil.colorList(messages);
    }

    public List<String> getNotices(){
        List<String> messages = new ArrayList<>();
        messages.addAll(notices);
        return StringUtil.colorList(messages);
    }

    public void loadNotices(List<String> strings){
        notices.addAll(strings);
    }
}
