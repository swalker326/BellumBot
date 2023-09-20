package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spells {
    public BellumBot bot;
    public String path;
    public ArrayList<Spell> activeSpells = new ArrayList<Spell>();
    public ArrayList<Spell> spells;
    public ArrayList<Spell> spellQueue = new ArrayList<Spell>();

    public Spells(BellumBot script) {
        this.bot = script;
    }

    public void LoadSpells(String filePath) {
        this.path = filePath;
        ArrayList<Spell> parsedSpells = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Spell currentSpell = null;

            while ((line = br.readLine()) != null) {
                // Trim and check for empty lines
                line = line.trim();
                if (line.isEmpty()) {
                    if (currentSpell != null) {
                        parsedSpells.add(currentSpell);
                        currentSpell = null; // reset the current spell
                    }
                    continue;
                }

                if (bot.debug == BellumBot.DEBUG.LOW) {
                    bot.script.print("LINE: " + line);
                }

                if (line.startsWith("SPELL")) {
                    String[] splitLine = line.split(" ", 2);
                    bot.script.capture("SPELL: " + splitLine[1]);
                    currentSpell = new Spell(bot.script, splitLine[1]);
                } else if (currentSpell != null) { // if we have an active spell
                    String[] splitLine = line.split(" ", 2);
                    switch (splitLine[0].toUpperCase()) {
                        case "TARGET":
                            currentSpell.setTarget(splitLine[1]);
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("TARGET: " + splitLine[1]);
                            });
                            break;
                        case "PREVENT":
                            currentSpell.prevent = splitLine[1];
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("PREVENT: " + splitLine[1]);
                            });
                            break;
                        case "CASTMESSAGES":
                            currentSpell.setCastMessage(splitLine[1]);
                            break;
                        case "COMMAND":
                            currentSpell.setCommand(splitLine[1]);
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("COMMAND: " + splitLine[1]);
                            });
                            break;
                        case "COST":
                            currentSpell.setCost(Integer.parseInt(splitLine[1]));
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("COST: " + splitLine[1]);
                            });
                            break;
                        case "LAG":
                            currentSpell.setLag(Integer.parseInt(splitLine[1]));
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("LAG: " + splitLine[1]);
                            });
                            break;
                        case "DURATION":
                            currentSpell.setDuration(Integer.parseInt(splitLine[1]));
                            bot.debug(BellumBot.DEBUG.LOW, () -> {
                                bot.script.print("DURATION: " + splitLine[1]);
                            });
                            break;
                        default:
                            break;
                    }
                }
            }

            // Handle the case where the last spell block doesn't end with an empty line
            if (currentSpell != null) {
                parsedSpells.add(currentSpell);
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

    public void saveToFile() throws IOException {
        if (this.path == null) {
            throw new IOException("No file path set");
        }
        try (FileWriter writer = new FileWriter(this.path)) { // replace with your actual file path
            for (Spell spell : spells) {
                writer.write(spell.getName() + (spell.isActive() ? ",active" : ",inactive") + "\n");
            }
        }
    }

    public Spell getSpellByPrevent(String prevent) {
        bot.script.print("Looking for spell with prevent: " + prevent);
        for (Spell spell : spells) {
            if (spell.prevent != null && spell.prevent.equals(prevent)) {
                return spell;
            }
        }
        return null;
    }

    public Spell getSpell(String name) {
        for (Spell spell : spells) {
            if (spell.name.equals(name)) {
                return spell;
            }
        }
        return null;
    }

    public void checkSpells(String text) {
        checkSpellsCastMessages(text);
        checkSpellFallMessages(text);
    }

    public void checkPrevents(String text) {
        Pattern preventPattern = Pattern.compile("You may again perform ([\\w\\s-'.\"]+) abilities.\n");
        Matcher preventMatcher = preventPattern.matcher(text);
        if (preventMatcher.find()) {
            String prevent = preventMatcher.group(1);
            activeSpells = removeSpell(prevent);
            Spell spell = getSpellByPrevent(prevent);
            if (spell != null) {
                bot.script.print("Adding prevent spell to queue: " + spell.name);
                spellQueue.add(spell);
            }
        }
        Pattern duplicatePreventPattern = Pattern.compile("You cannot perform ([\\w\\s\\-']+) abilities again yet \\(type 'prevention'\\).");
        Matcher duplicatePreventMatcher = duplicatePreventPattern.matcher(text);
        if (duplicatePreventMatcher.find()) {
            Spell duplicate = getSpellByPrevent(duplicatePreventMatcher.group(1).trim());
            if (duplicate != null) {
                duplicate.setOnCooldown(true);
            } else {
                bot.script.print("Could not find spell with prevent: " + duplicatePreventMatcher.group(1));
            }
        }
    }

    public void checkSpellsCastMessages(String text) {
        checkPrevents(text);
        for (Spell spell : spells) {
            if (spell.checkSpellCastMessages(text)) {
                bot.script.print("Adding active spell " + spell.name);
                activeSpells.add(spell);
                spell.setActive(true);
            }
        }
    }

    public void checkSpellFallMessages(String text) {
        if (text.startsWith("You are no longer affected by: ")) {
            String[] splitEvent = text.split(":");
            String spellName = splitEvent[1].trim().replace(".", "");
            bot.script.print("Removing spell: " + spellName);
            Spell spell = getSpell(spellName);
            if (spell != null) {
                bot.script.print("Removing spell: " + spell.name);
                activeSpells = removeSpell(spell.name);
                spell.setActive(false);
                if (spell.prevent == null) {
                    bot.script.print("Adding spell to queue: " + spell.name);
                    spellQueue.add(spell);
                }
            }
        }
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
                if (!spell.getOnCooldown() && spell.cost <= bot.prompt.mana[0]) {
                    spellQueue.add(spell);
                }
            }
        }
        if (!spellQueue.isEmpty()) {
            Spell spell = spellQueue.get(0);
            if (bot.prompt.lag <= 0 && !spell.getOnCooldown() && bot.prompt.mana[0] >= spell.cost && bot.state.battleState != State.BATTLESTATE.COMBAT) {
                spell.doSpell();
                spellQueue = removeQueueSpell(spell.name);
            }
        }
    }
}
