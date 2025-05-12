// src/main/java/ma/formation/enums/GroupeSanguin.java
package ma.formation.enums;

public enum GroupeSanguin {
    A_POSITIF("A+"),
    A_NEGATIF("A-"),
    B_POSITIF("B+"),
    B_NEGATIF("B-"),
    AB_POSITIF("AB+"),
    AB_NEGATIF("AB-"),
    O_POSITIF("O+"),
    O_NEGATIF("O-"),
    INCONNU("Inconnu");

    private final String label;

    GroupeSanguin(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static GroupeSanguin fromLabel(String label) {
        for (GroupeSanguin gs : values()) {
            if (gs.label.equalsIgnoreCase(label)) {
                return gs;
            }
        }
        return INCONNU;
    }
}