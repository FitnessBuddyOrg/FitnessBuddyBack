package api.fitnessbuddyback.enumeration;

import lombok.Getter;

@Getter
public enum ShareType {
    PUBLIC,
    PRIVATE,
    SHARED;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}