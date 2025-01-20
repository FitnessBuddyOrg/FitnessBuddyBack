package api.fitnessbuddyback.enumeration;

import lombok.Getter;

@Getter
public enum Category {
    ARMS,
    BACK,
    CHEST,
    LEGS,
    SHOULDERS,
    ABS;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}