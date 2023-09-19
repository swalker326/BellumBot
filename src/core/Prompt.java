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
    public boolean flagged = false;
    public String charName = "";
    public String reply = "";
    public String tank = "";
    public String tankCondition = "";
    public String opponent = "";
    public String opponentCondition = "";
    public Pattern promptPattern = Pattern.compile("^\\[L:(\\d+)] \\[(\\w+) H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)] \\[A:(-?\\d+)] \\[]");
    public Pattern promptLineTwoPattern = Pattern.compile("^\\[PK:(\\w+)?] \\[XP:(\\d+)] \\[G:(\\d+)] \\[Reply:(\\w+)?]");

    public Pattern battlePromptPattern = Pattern.compile("^\\[(\\w+)]\\((\\w+)\\) \\[([\\w\\s-]+)]\\(([\\w\\s-]+)\\)");
    public Pattern battlePromptLineTwoPattern = Pattern.compile("^\\[L:(\\d+)] \\[H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)] \\[A:(-?\\d+)] \\[]");


    public Prompt() {
        this.health = new int[]{0, 0};
        this.mana = new int[]{0, 0};
        this.endurance = new int[]{0, 0};
        this.spirit = new int[]{0, 0};

    }

    public void processBattlePrompt(BellumBot bot, String prompt) {
        Matcher promptMatcher = this.battlePromptPattern.matcher(prompt);
        if (promptMatcher.find()) {
            this.lag = Integer.parseInt(promptMatcher.group(1));
            this.tank = promptMatcher.group(2);
            this.tankCondition = promptMatcher.group(3);
            this.opponent = promptMatcher.group(4);
            this.opponentCondition = promptMatcher.group(5);

            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.script.print("BATTLE PROMPT MATCH -->: " +
                        "\n Attacker: " + promptMatcher.group(1) +
                        "\n Attacker Target: " + promptMatcher.group(2) +
                        "\n Defender: " + promptMatcher.group(3) +
                        "\n Defender Target: " + promptMatcher.group(4));
            });
        }
    }

    public Prompt ProcessPrompt(BellumBot bot, String prompt) {
        Matcher promptMatcher = this.promptPattern.matcher(prompt);
        if (promptMatcher.find()) {
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
            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("BASIC PROMPT LN 1 -->: " +
                        "\n Lag: " + this.lag +
                        "\n Health: " + this.health[0] + "/" + this.health[1] +
                        "\n Mana: " + this.mana[0] + "/" + this.mana[1] +
                        "\n Spirit: " + this.spirit[0] + "/" + this.spirit[1] +
                        "\n Endurance: " + this.endurance[0] + "/" + this.endurance[1]);
            });
        }
        Matcher promptMather = this.promptLineTwoPattern.matcher(prompt);
        if (promptMather.find()) {
            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("group 1 " + promptMather.group(1));
            });
            String groupOne = promptMather.group(1);
            this.flagged = groupOne != null && groupOne.equals("PKer");
            this.xp = Integer.parseInt(promptMather.group(2));
            this.gold = Integer.parseInt(promptMather.group(3));
            this.reply = promptMather.group(4);

            bot.debug(BellumBot.DEBUG.MED, () -> {
                bot.console.capture("PROMPT LINE 2 -->: " +
                        "\n PK: " + this.flagged +
                        "\n XP: " + this.xp +
                        "\n G: " + this.gold +
                        "\n Reply: " + this.reply);
            });
        }
        processBattlePrompt(bot, prompt);

        return this;
    }
}
