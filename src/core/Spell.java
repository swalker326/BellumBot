package core;

import com.lsd.umc.script.ScriptInterface;

public class Spell {
    protected Boolean isPrevent = false;
    protected String name;
    protected String target;
    protected String command;
    protected String pool;
    protected String preventMessage;
    protected String[] castMessages;
    protected String[] recastMessages;
    protected int cost;
    protected int lag;
    protected int duration;
    protected int cooldown;
    protected ScriptInterface script = null;

    public Spell(ScriptInterface script, String name, String target, String[] castMessages, String[] recastMessages, String[] failMessages, int cost, int lag, int duration, int cooldown) {
        this.script = script;
        this.name = name;
        this.target = target;
        this.castMessages = castMessages;
        this.recastMessages = recastMessages;
        this.cost = cost;
        this.lag = lag;
        this.duration = duration;
        this.cooldown = cooldown;
    }

    public Spell(ScriptInterface script, String name, String[] castMessages, String[] recastMessages, String command) {
        this.script = script;
        this.name = name;
        this.target = "self";
        this.castMessages = castMessages;
        this.recastMessages = recastMessages;
        this.command = command;
    }

    public void doSpell() {
        if (this.command != null) {
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

    public void setCastMessages(String[] castMessages) {
        this.castMessages = castMessages;
    }

    public void setRecastMessages(String[] recastMessages) {
        this.recastMessages = recastMessages;
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

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getName() {
        return this.name;
    }

    public String getTarget() {
        return this.target;
    }

    public String[] getCastMessages() {
        return this.castMessages;
    }

    public String[] getRecastMessages() {
        return this.recastMessages;
    }

    public int getCost() {
        return this.cost;
    }

    public int getLag() {
        return this.lag;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getIsPrevent() {
        return this.isPrevent;
    }

    public void setIsPrevent(Boolean isPrevent) {
        this.isPrevent = isPrevent;
    }

    public void addCastMessage(String message) {
        String[] newCastMessages = new String[this.castMessages.length + 1];
        for (int i = 0; i < this.castMessages.length; i++) {
            newCastMessages[i] = this.castMessages[i];
        }
        newCastMessages[this.castMessages.length] = message;
        this.castMessages = newCastMessages;
    }

    public void addRecastMessage(String message) {
        String[] newRecastMessages = new String[this.recastMessages.length + 1];
        for (int i = 0; i < this.recastMessages.length; i++) {
            newRecastMessages[i] = this.recastMessages[i];
        }
        newRecastMessages[this.recastMessages.length] = message;
        this.recastMessages = newRecastMessages;
    }
}
