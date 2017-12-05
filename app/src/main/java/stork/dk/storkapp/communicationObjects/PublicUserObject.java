package stork.dk.storkapp.communicationObjects;

/**
 * @author Johannes Ernstsen
 */
public class PublicUserObject {
    private int userId;
    private String name;
    private String mail;

    @SuppressWarnings("unused")
    public PublicUserObject() {
    }

    @SuppressWarnings("unused")
    public PublicUserObject(int userId, String name, String mail) {
        this.userId = userId;
        this.name = name;
        this.mail = mail;
    }

    @SuppressWarnings("unused")
    public int getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getMail() {
        return mail;
    }

    @SuppressWarnings("unused")
    public void setMail(String mail) {
        this.mail = mail;
    }
}
