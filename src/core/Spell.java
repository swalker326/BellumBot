package core;

import com.lsd.umc.script.ScriptInterface;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.regex.Pattern;

public class Spell {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Boolean onCooldown;
    private Boolean isActive = false;
    protected String name;
    protected String prevent;
    protected String target;
    protected String command;
    protected String pool;
    protected Pattern castMessage;
    protected int cost;
    protected int lag;
    protected int duration;
    protected ScriptInterface script = null;

    public Spell(ScriptInterface script, String name) {
        this.script = script;
        this.name = name;
        this.onCooldown = false;
        this.isActive = false;
    }

    public Boolean checkSpellCastMessages(String text) {
        return this.castMessage.matcher(text).find();
    }

    public void doSpell() {
        script.print("Casting " + this.name);
        if (this.command != null && !this.isActive) {
            script.send(this.command);
        } else {
            script.capture("failed to do " + this.name + " command is not defined");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPool(String pool) {
        switch (pool.toLowerCase()) {
            case "mana":
                this.pool = "mana";
                break;
            case "spirit":
                this.pool = "spirit";
                break;
        }
        this.pool = pool;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setCastMessage(String castMessage) {
        if (!castMessage.startsWith("^")) {
            castMessage = "^" + castMessage;
        }
        this.castMessage = Pattern.compile(castMessage);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setLag(int lag) {
        this.lag = lag;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOnCooldown(Boolean cooldown) {
        this.onCooldown = cooldown;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getOnCooldown() {
        return this.onCooldown;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setActive(boolean active) {
        boolean oldActive = this.isActive;
        this.isActive = active;
        support.firePropertyChange("isActive", oldActive, active);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

}
