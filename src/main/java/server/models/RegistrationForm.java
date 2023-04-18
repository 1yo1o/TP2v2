package server.models;

import java.io.Serializable;

public class RegistrationForm implements Serializable {
    private String prenom;
    private String nom;
    private String email;
    private String matricule;
    private Course course;

    /**
     *
     * @param prenom Un string représentant le prenom de la personne qui veut s'inscrire.
     * @param nom Un string représentant le nom de la personne qui veut s'inscrire.
     * @param email Un string représentant l'email de la personne qui veut s'inscrire.
     * @param matricule Un string représentant la matricule de la personne qui veut s'inscrire.
     * @param course Un Course qui contient tout l'information necessaire au cour l'étudiant veut s'inscrire.
     */
    public RegistrationForm(String prenom, String nom, String email, String matricule, Course course) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.matricule = matricule;
        this.course = course;
    }

    /**
     *
     * @return Un string représentant le prenom de la personne qui veut s'inscrire.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     *
     * @param prenom Associe un string représentant le prenom de la personne qui veut s'inscrire.
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     *
     * @return Un string représentant le nom de la personne qui veut s'inscrire.
     */
    public String getNom() {
        return nom;
    }

    /**
     *
     * @param nom Associe un string représentant le nom de la personne qui veut s'inscrire.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     *
     * @return Un string représentant le email de la personne qui veut s'inscrire.
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email Associe un string représentant le email de la personne qui veut s'inscrire.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return Un string représentant la matricule de la personne qui veut s'inscrire.
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     *
     * @param matricule Associe un string représentant la matricule de la personne qui veut s'inscrire.
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     *
     * @return Un Course qui contient tout l'information necessaire au cour l'étudiant veut s'inscrire.
     */
    public Course getCourse() {
        return course;
    }

    /**
     *
     * @param course Associe un Course qui contient tout l'information necessaire au cour l'étudiant veut s'inscrire.
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     *
     * @return Une représentation textuelle de l'information contenue dans une classe RegistrationForm.
     */
    @Override
    public String toString() {
        return "InscriptionForm{" + "prenom='" + prenom + '\'' + ", nom='" + nom + '\'' + ", email='" + email + '\'' + ", matricule='" + matricule + '\'' + ", course='" + course + '\'' + '}';
    }

}