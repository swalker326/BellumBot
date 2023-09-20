package core;

import com.lsd.umc.script.ScriptInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prompt {
    public int lag = 0;
    public int[] health = {0, 0};
    public int[] mana = {0, 0};
    public int[] endurance = {0, 0};
    public int[] spirit = {0, 0};
    public int xp = 0;
    public int gold = 0;
    public int illume;
    public int satio;
    public int lif;
    public boolean flagged = false;
    public String charName = "";
    public String reply = "";
    public String tank = "";
    public String tankCondition = "";
    public String opponent = "";
    public String opponentCondition = "";
    public Pattern promptPattern = Pattern.compile("^\\[L:(\\d+)] \\[(\\w+) H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)] \\[A:(-?\\d+)] \\[(?:I:(\\d+) S:(\\d+) L:(\\d+))?]");
    public Pattern promptLineTwoPattern = Pattern.compile("^\\[PK:(\\w+)?] \\[XP:(\\d+)] \\[G:(\\d+)] \\[Reply:(\\w+)?]");

    // battle:     [L:0] [H:79596/79628 M:76646/76646 S:76700/76700 E:76570/76628] [A:-992] [I:21 S:89 L:12]
    // no battle:  [L:0] [Bellum H:79561/79628 M:76646/76646 S:76700/76700 E:76570/76628] [A:-992] [I:21 S:89 L:12]


    public Pattern battlePromptPattern = Pattern.compile("^\\[(\\w+)]\\(([\\w\\s]+)\\) \\[([\\w\\s-]+)]\\(([\\w\\s-]+)\\)");
    public Pattern battlePromptLineTwoPattern = Pattern.compile("^\\[L:(\\d+)] \\[H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)] \\[A:(-?\\d+)] \\[(?:I:(\\d+) S:(\\d+) L:(\\d+))?]");


    public Prompt() {
        this.health = new int[]{0, 0};
        this.mana = new int[]{0, 0};
        this.endurance = new int[]{0, 0};
        this.spirit = new int[]{0, 0};

    }

    public void processBattlePrompt(BellumBot bot, String prompt) {
        Matcher promptMatcher = this.battlePromptPattern.matcher(prompt);
        if (promptMatcher.find()) {
            bot.state.battleState = State.BATTLESTATE.COMBAT;
            this.tank = promptMatcher.group(1);
            this.tankCondition = promptMatcher.group(2);
            this.opponent = promptMatcher.group(3);
            this.opponentCondition = promptMatcher.group(4);

            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("BATTLE PROMPT -->: " +
                        "Attacker: " + promptMatcher.group(1) +
                        " Attacker Condition: (" + promptMatcher.group(2) + ")" +
                        " Defender: " + promptMatcher.group(3) +
                        " Defender Condition: (" + promptMatcher.group(4) + ")");
            });
        }
        Matcher promptMatcherLineTwo = this.battlePromptLineTwoPattern.matcher(prompt);
        if (promptMatcherLineTwo.find()) {
            this.lag = Integer.parseInt(promptMatcherLineTwo.group(1));
            this.health[0] = Integer.parseInt(promptMatcherLineTwo.group(2));
            this.health[1] = Integer.parseInt(promptMatcherLineTwo.group(3));
            this.mana[0] = Integer.parseInt(promptMatcherLineTwo.group(4));
            this.mana[1] = Integer.parseInt(promptMatcherLineTwo.group(5));
            this.spirit[0] = Integer.parseInt(promptMatcherLineTwo.group(6));
            this.spirit[1] = Integer.parseInt(promptMatcherLineTwo.group(7));
            this.endurance[0] = Integer.parseInt(promptMatcherLineTwo.group(8));
            this.endurance[1] = Integer.parseInt(promptMatcherLineTwo.group(9));
            if (promptMatcherLineTwo.group(11) != null) {
                this.illume = Integer.parseInt(promptMatcherLineTwo.group(11));
                this.satio = Integer.parseInt(promptMatcherLineTwo.group(12));
                this.lif = Integer.parseInt(promptMatcherLineTwo.group(13));
            }

            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture(
                        "BATTLE PROMPT LINE 2 -->: " +
                                " Lag: " + this.lag +
                                " Health: " + this.health[0] + "/" + this.health[1] +
                                " Mana: " + this.mana[0] + "/" + this.mana[1] +
                                " Spirit: " + this.spirit[0] + "/" + this.spirit[1] +
                                " Endurance: " + this.endurance[0] + "/" + this.endurance[1] +
                                " Illume: " + this.illume +
                                " Satio: " + this.satio +
                                " Lif: " + this.lif
                );
            });
        }
    }

    public Prompt ProcessPrompt(BellumBot bot, String prompt) {
        Matcher promptMatcher = this.promptPattern.matcher(prompt);
        if (promptMatcher.find()) {
            bot.state.battleState = State.BATTLESTATE.NONCOMBAT;
            this.lag = Integer.parseInt(promptMatcher.group(1));
            this.charName = promptMatcher.group(3);
            this.health[0] = Integer.parseInt(promptMatcher.group(3));
            this.health[1] = Integer.parseInt(promptMatcher.group(4));
            this.mana[0] = Integer.parseInt(promptMatcher.group(5));
            this.mana[1] = Integer.parseInt(promptMatcher.group(6));
            this.spirit[0] = Integer.parseInt(promptMatcher.group(7));
            this.spirit[1] = Integer.parseInt(promptMatcher.group(8));
            this.endurance[0] = Integer.parseInt(promptMatcher.group(9));
            this.endurance[1] = Integer.parseInt(promptMatcher.group(10));
            if (promptMatcher.group(12) != null) {
                this.illume = Integer.parseInt(promptMatcher.group(12));
                this.satio = Integer.parseInt(promptMatcher.group(13));
                this.lif = Integer.parseInt(promptMatcher.group(14));
            }

            bot.spells.SpellUp();
            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("PROMPT -->: " +

                        " Lag: " + this.lag +
                        " Health: " + this.health[0] + "/" + this.health[1] +
                        " Mana: " + this.mana[0] + "/" + this.mana[1] +
                        " Spirit: " + this.spirit[0] + "/" + this.spirit[1] +
                        " Endurance: " + this.endurance[0] + "/" + this.endurance[1] +
                        " Illume: " + this.illume +
                        " Satio: " + this.satio +
                        " Lif: " + this.lif);

            });
        }
        Matcher promptMather = this.promptLineTwoPattern.matcher(prompt);
        if (promptMather.find()) {
            String groupOne = promptMather.group(1);
            this.flagged = groupOne != null && groupOne.equals("PKer");
            this.xp = Integer.parseInt(promptMather.group(2));
            this.gold = Integer.parseInt(promptMather.group(3));
            this.reply = promptMather.group(4);

            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("PROMPT LINE 2 -->: " +
                        " PK: " + this.flagged +
                        " XP: " + this.xp +
                        " G: " + this.gold +
                        " Reply: " + this.reply);
            });
        }
        processBattlePrompt(bot, prompt);

        return this;
    }
}
