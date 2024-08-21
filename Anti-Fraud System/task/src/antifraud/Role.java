package antifraud;

public enum Role {
    MERCHANT("MERCHANT"),
    SUPPORT("SUPPORT"),
    ADMINISTRATOR("ADMINISTRATOR");

    private String title;

    Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
