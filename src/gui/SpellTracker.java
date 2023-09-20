package gui;

import core.Spell;
import core.Spells;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class SpellTracker extends JFrame {
    private final Spells spellManager;
    private final DefaultListModel<String> spellListModel = new DefaultListModel<>();
    private final JList<String> spellList = new JList<>(spellListModel);
    private final JScrollPane scrollPane = new JScrollPane(spellList);
    private final JButton editButton = new JButton("Edit");
    private final JButton saveButton = new JButton("Save");

    // Edit Form Components
    private final JPanel editPanel = new JPanel();
    private final JTextField spellNameField = new JTextField(15);
    private final JCheckBox isActiveCheckbox = new JCheckBox("Active");
    private final JButton updateButton = new JButton("Update");

    public SpellTracker(Spells spellManager) {
        this.spellManager = spellManager;

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(editButton);
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        editButton.addActionListener(e -> showEditForm());
        saveButton.addActionListener(e -> saveSpells());

        updateSpellList();

        // Set up the listener for live updates
        for (Spell spell : spellManager.spells) {
            spell.addPropertyChangeListener(propertyChangeListener);
        }
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spellList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    handleSpellSelection();
                }
            }
        });
    }

    private void handleSpellSelection() {
        String selectedSpellName = spellList.getSelectedValue().split(":")[0].trim();
        Spell selectedSpell = spellManager.getSpell(selectedSpellName);
        System.out.println("Selected spell: " + selectedSpell.getName());
        // Now, 'selectedSpell' holds the selected spell instance. Use it for further operations as needed.
    }

    private void showEditForm() {
        String selectedSpellName = spellList.getSelectedValue();
        if (selectedSpellName != null) {
            Spell selectedSpell = spellManager.getSpell(selectedSpellName);
            spellNameField.setText(selectedSpell.getName());
            isActiveCheckbox.setSelected(selectedSpell.isActive());

            int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Spell", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                selectedSpell.setName(spellNameField.getText());
                selectedSpell.setActive(isActiveCheckbox.isSelected());
            }
        }
    }

    private void saveSpells() {
        try {
            spellManager.saveToFile(); // Assuming your Spells class has this method to save changes back to the file.
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save spells to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSpellList() {
        spellListModel.clear();
        for (Spell spell : spellManager.spells) {
            String status = spell.isActive() ? "Active" : "Inactive";
            spellListModel.addElement(spell.getName() + ":" + status);
        }
    }

    PropertyChangeListener propertyChangeListener = evt -> {
        if ("isActive".equals(evt.getPropertyName()) || "name".equals(evt.getPropertyName())) {
            updateSpellList();
        }
    };

    public void showTracker() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public void hideTracker() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
        });
    }
}
