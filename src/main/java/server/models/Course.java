package server.models;

import java.io.Serializable;

public class Course implements Serializable {

    private String name;
    private String code;
    private String session;

    /**
     *
     * @param name Un string représentant le nom du cour.
     * @param code Un string représentant le code du cour.
     * @param session Un string représant la session où le cour se donne.
     */
    public Course(String name, String code, String session) {
        this.name = name;
        this.code = code;
        this.session = session;
    }

    /**
     *
     * @return Un string représentant le nom du cour.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name Asssocie un string représentant le nom du cour.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Un string représentant le code du cour.
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code Associe un string représentant le code du cour.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return Un string représentant la session du cour.
     */
    public String getSession() {
        return session;
    }

    /**
     *
     * @param session Associe un string représentant la session où le cour se donne.
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     *
     * @return Une représentation textuelle de l'information contenue dans une classe Course.
     */
    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                ", session=" + session +
                '}';
    }

}