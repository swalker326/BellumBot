package core;

import com.lsd.umc.script.ScriptInterface;
import gui.SpellTracker;

public class BellumBot {
    protected ScriptInterface script = null;
    protected SpellTracker spellTracker = null;
    protected BellumBotConsole console = new BellumBotConsole(this);
    public Spells spells = null;
    protected Prompt prompt = null;
    public State state = null;
    private final Mob[] mobs = null;
    private final Room room = null;

    public enum DEBUG {
        OFF(0), //0
        LOW(1), //1
        MED(2), //2
        HIGH(3),
        ; //3
        private final int value;

        DEBUG(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    protected DEBUG debug = DEBUG.OFF;

    public BellumBot() {
    }

    public void debug(DEBUG level, Runnable action) {
        if (this.debug.getValue() >= level.getValue()) {
            action.run();
        }
    }

    public void ConsoleCapture(String message) {
        script.capture(message);
    }

    public String Console(String args) {
        if (console.isVisible()) {
            console.hideConsole();
        } else {
            console.showConsole();
        }
        return "";
    }

    public String loadSpellsFromFile(String args) {
        spells.LoadSpells(args);
        this.spellTracker = new SpellTracker(spells);
        return "";
    }

    public String buff(String args) {
        spells.SpellUp();
        return "";
    }

    public void setDebug(String args) {
        if (args != null) {
            switch (args.toUpperCase()) {
                case "1":
                    this.debug = DEBUG.LOW;
                    break;
                case "2":
                    this.debug = DEBUG.MED;
                    break;
                case "3":
                    this.debug = DEBUG.HIGH;
                    break;
                default:
                    this.debug = DEBUG.OFF;
                    break;
            }
        }
    }

    public String SpellTracker(String args) {
        if (spellTracker.isVisible()) {
            spellTracker.hideTracker();
        } else {
            spellTracker.showTracker();
        }
        return "";
    }

    public void init(ScriptInterface script) {

        setDebug(script.getVariable("DEBUG"));

        this.script = script;
        this.prompt = new Prompt();
        this.state = new State();
        this.spells = new Spells(this);
        script.registerCommand("LOADSPELLS", "core.BellumBot", "loadSpellsFromFile");
        script.registerCommand("BUFF", "core.BellumBot", "buff");
        script.registerCommand("CONSOLE", "core.BellumBot", "Console");
        script.registerCommand("SPELLS", "core.BellumBot", "SpellTracker");
        script.print("BellumBot initialized");
    }

    public void IncomingEvent(ScriptInterface script) {
        debug(DEBUG.HIGH, () -> console.capture("EVENT -->" + script.getEvent()));
        String text = script.getText();
        //with DEBUG high we can log raw events to a file
        debug(DEBUG.HIGH, () -> EventLogger.logEvent(script));
        setDebug(script.getVariable("DEBUG"));
        prompt = prompt.ProcessPrompt(this, text);
        state = state.ProcessState(text);
        spells.checkSpells(text);
    }

}