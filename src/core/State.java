package core;

public class State {
    enum BATTLESTATE {
        COMBAT,
        NONCOMBAT,
        UNKNOWN
    }

    enum POSITION {
        STANDING,
        RESTING,
        SLEEPING,
        UNKNOWN
    }

    public BATTLESTATE battleState;
    public POSITION position;

    public State() {
        this.battleState = BATTLESTATE.UNKNOWN;
        this.position = State.POSITION.UNKNOWN;
    }

    public void ProcessPosition(String event) {

        if (event.contains("While you're sleeping? Maybe you should wake up first?") || event.contains("You lie down and go to sleep.")) {
            this.position = POSITION.SLEEPING;
        } else if (event.contains("You are already resting.") || event.contains("You rest.")) {
            this.position = POSITION.RESTING;
        } else if (event.contains("You stand up.") || event.contains("You wake up and climb to your feet.") || event.contains("You are already awake.") || event.contains("You are already standing.")) {
            this.position = POSITION.STANDING;
        }
        System.out.println("Position: " + this.position);
    }

    public void ProcessBattleState(String event) {
        if (event.contains("You are already in combat!") || event.contains("You are already in combat.")) {
            this.battleState = BATTLESTATE.COMBAT;
        } else if (event.contains("You are idle.")) {
            this.battleState = BATTLESTATE.NONCOMBAT;
        }
        System.out.println("Battle State: " + this.battleState);
    }

    public State ProcessState(String event) {
        this.ProcessPosition(event);
        this.ProcessBattleState(event);
        return this;
    }

}
