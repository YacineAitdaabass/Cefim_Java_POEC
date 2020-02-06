package eu.cefim2.java.model;

public class Client {

    private int id;
    private String mail;
    private String password;

    public Client() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {

        return "Client{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                '}';
    }
}
