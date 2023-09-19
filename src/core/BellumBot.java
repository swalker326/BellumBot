package core;

import com.lsd.umc.script.ScriptInterface;

public class BellumBot {
    protected ScriptInterface script = null;
    protected Prompt prompt = null;
    private State state = null;
    private Mob[] mobs = null;
    private Room room = null;

    enum DEBUG {
        OFF, //0
        LOW, //1
        MED, //2
        HIGH, //3
    }

    protected DEBUG debug = DEBUG.OFF;

    public BellumBot() {
    }

    public String test(String args) {
        script.print("Test Command Called");
        return "";
    }

    public void toggleDebug(String args) {
        if (args != null) {
            switch (args.toUpperCase()) {
                case "OFF":
                    this.debug = DEBUG.OFF;
                    break;
                case "LOW":
                    this.debug = DEBUG.LOW;
                    break;
                case "MED":
                    this.debug = DEBUG.MED;
                    break;
                case "HIGH":
                    this.debug = DEBUG.HIGH;
                    break;
                default:
                    this.debug = DEBUG.OFF;
                    break;
            }
        }
    }

    public void init(ScriptInterface script) {
        toggleDebug(script.getVariable("DEBUG"));

        this.script = script;
        this.prompt = new Prompt();
        this.state = new State();
        script.registerCommand("TEST", "core.BellumBot", "test");
        script.print("BellumBot initialized");
    }

    public void IncomingEvent(ScriptInterface script) {
        if (this.debug == DEBUG.MED) {
            script.print("EVENT -->" + script.getEvent());
        }
        toggleDebug(script.getVariable("DEBUG"));
        prompt = prompt.ProcessPrompt(this, script.getText());
        state = state.ProcessState(script.getText());
    }

}