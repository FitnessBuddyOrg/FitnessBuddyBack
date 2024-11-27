package api.fitnessbuddyback.exeption;

public class UserAlreadyExistsException extends RuntimeException {
    public final String username;

    public UserAlreadyExistsException(String username) {
        super(String.format("L'utilisateur %s existe déjà", username));
        this.username = username;
    }
}
