package core;

import com.lsd.umc.script.ScriptInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prompt {
    //make a regex that matches the prompt: [L:2000] [Bellum H:79628/79628 M:76646/76646 S:76700/76700 E:76628/76628] [A:-1000] [][PK:] [XP:99833443] [G:381567] [Reply:Furio]
    public Pattern promptPattern = Pattern.compile("\\[L:(\\d+)\\] \\[Bellum H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)\\] \\[A:(\\d+)\\] \\[\\]\\[PK:\\] \\[XP:(\\d+)\\] \\[G:(\\d+)\\] \\[Reply:(\\w+)\\]");
    public int[] health = {0, 0};
    public int[] mana = {0, 0};
    public int[] endurance = {0, 0};
    public int[] spirit = {0, 0};

    public Prompt() {
        this.promptPattern = Pattern.compile("\\[L:(\\d+)] \\[Bellum H:(\\d+)/(\\d+) M:(\\d+)/(\\d+) S:(\\d+)/(\\d+) E:(\\d+)/(\\d+)] \\[A:(-?\\d+)] \\[]");

        this.health = new int[]{0, 0};
        this.mana = new int[]{0, 0};
        this.endurance = new int[]{0, 0};
        this.spirit = new int[]{0, 0};

    }

    public Prompt ProcessPrompt(BellumBot bot, String prompt) {
        //parse health, mana, endurance, spirit from a regex that matches the prompt
        Matcher promptMatcher = this.promptPattern.matcher(prompt);
        if (promptMatcher.find()) {
            this.health[0] = Integer.parseInt(promptMatcher.group(2));
            this.health[1] = Integer.parseInt(promptMatcher.group(3));
            this.mana[0] = Integer.parseInt(promptMatcher.group(4));
            this.mana[1] = Integer.parseInt(promptMatcher.group(5));
            this.spirit[0] = Integer.parseInt(promptMatcher.group(6));
            this.spirit[1] = Integer.parseInt(promptMatcher.group(7));
            this.endurance[0] = Integer.parseInt(promptMatcher.group(8));
            this.endurance[1] = Integer.parseInt(promptMatcher.group(9));
        }

        if (bot.debug == BellumBot.DEBUG.LOW) {
            bot.script.print("PROMPT -->: Health: " + this.health[0] + "/" + this.health[1] + " Mana: " + this.mana[0] + " Endurance: " + this.endurance[0] + " Spirit: " + this.spirit[0]);
        }
        return this;
    }
}
