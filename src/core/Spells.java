package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Spells {
    public BellumBot bot;
    public ArrayList<Spell> activeSpells = new ArrayList<Spell>();
    public ArrayList<Spell> spells;
    public ArrayList<Spell> spellQueue = new ArrayList<Spell>();

    public Spells(BellumBot script) {
        this.bot = script;
    }

    public void LoadSpells(String filePath) {
        //read a text file and parse it into spells
        //the format of the text file should be:
        //SPELL name
        //TARGET target
        //CASTMESSAGES message1, message2, message3
        //RECASTMESSAGES message1, message2, message3
        //FAILMESSAGES message1, message2, message3
        //COST cost
        //LAG lag
        //DURATION duration
        //COOLDOWN cooldown
        //END
        //if target is not defined, it will default to self
        //required: name, castmessages, recastmessages, cost, lag
        ArrayList<Spell> parsedSpells = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (bot.debug == BellumBot.DEBUG.LOW) {
                    bot.script.print("LINE: " + line);
                }
                if (line.startsWith("SPELL")) {
                    String[] splitLine = line.split(" ", 2);
                    Spell spell = new Spell(bot.script, splitLine[1], "", new String[]{}, new String[]{}, new String[]{}, 0, 0, 0, 0);
                    while ((line = br.readLine()) != null) {
                        splitLine = line.split(" ", 2);
                        bot.script.print("LINE: " + line);
                        if (line.matches("^\\s*$")) {
                            System.out.println("EMPTY LINE, maybe a break? adding spell to list");
                            parsedSpells.add(spell);
                        }
                        switch (splitLine[0].toUpperCase()) {
                            case "TARGET":
                                spell.setTarget(splitLine[1]);
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("TARGET: " + splitLine[1]);
                                }
                                break;
                            case "CASTMESSAGES":
                                spell.setCastMessages(splitLine[1].split(","));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("CASTMESSAGES: " + splitLine[1]);
                                }
                                break;
                            case "COMMAND":
                                spell.setCommand(splitLine[1]);
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("COMMAND: " + splitLine[1]);
                                }
                                break;
                            case "RECASTMESSAGES":
                                spell.setRecastMessages(splitLine[1].split(","));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("RECASTMESSAGES: " + splitLine[1]);
                                }
                                break;
                            case "COST":
                                spell.setCost(Integer.parseInt(splitLine[1]));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("COST: " + splitLine[1]);
                                }
                                break;
                            case "LAG":
                                spell.setLag(Integer.parseInt(splitLine[1]));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("LAG: " + splitLine[1]);
                                }
                                break;
                            case "DURATION":
                                spell.setDuration(Integer.parseInt(splitLine[1]));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("DURATION: " + splitLine[1]);
                                }
                                break;
                            case "COOLDOWN":
                                spell.setCooldown(Integer.parseInt(splitLine[1]));
                                if (bot.debug == BellumBot.DEBUG.LOW) {
                                    bot.script.print("COOLDOWN: " + splitLine[1]);
                                }
                                break;

                            default:
                                break;
                        }
                    }
                }
//                if (line.equals("END")) {
//                    spells = parsedSpells;
//                    return;
//                }
            }
            this.spells = parsedSpells;
            bot.script.print("Loaded " + parsedSpells.size() + " spells");
            for (Spell spell : parsedSpells) {
                bot.script.print("Added: " + spell.name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Spell getSpell(String name) {
        for (Spell spell : spells) {
            if (spell.name.equals(name)) {
                return spell;
            }
        }
        return null;
    }

    public ArrayList<Spell> removeSpell(String name) {
        ArrayList<Spell> newSpells = new ArrayList<>();
        for (Spell spell : spells) {
            if (!spell.name.equals(name)) {
                newSpells.add(spell);
            }
        }
        return newSpells;
    }

    public ArrayList<Spell> removeQueueSpell(String name) {
        ArrayList<Spell> newSpells = new ArrayList<>();
        for (Spell spell : spellQueue) {
            if (!spell.name.equals(name)) {
                newSpells.add(spell);
            }
        }
        return newSpells;
    }

    public void SpellUp() {
        if (activeSpells.isEmpty() && !spells.isEmpty()) {
            for (Spell spell : spells) {
                if (spell.cooldown <= 0 && spell.cost <= bot.prompt.mana[0]) {
                    spellQueue.add(spell);
                }
            }
        }
        if (!spellQueue.isEmpty()) {
            Spell spell = spellQueue.get(0);
            if (bot.prompt.lag <= 0 && spell.cooldown <= 0 && bot.prompt.mana[0] >= spell.cost && bot.state.battleState != State.BATTLESTATE.COMBAT) {
                spell.doSpell();
                spellQueue = removeQueueSpell(spell.name);
            }
        }
    }
}
