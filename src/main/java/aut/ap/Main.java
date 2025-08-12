package aut.ap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Users alreadyLoggedin;
    private static SessionFactory sessionFactory;
    private static void setUpSessionFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
    }
    private static void closeSessionFactory() {
        sessionFactory.close();
    }




    public static void main(String[] args){
        setUpSessionFactory();






        closeSessionFactory();
    }



    private static void signIn() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.print("first name: ");
        String firstName = scanner.nextLine();
        System.out.print("last name: ");
        String lastName = scanner.nextLine();
        System.out.print("age: ");
        Integer age = Integer.parseInt(scanner.nextLine());
        System.out.print("email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("password: ");
        String password = scanner.nextLine();

        if (password.length() < 8) {
            System.out.println("weak password, it must be at least 8 characters.");
            session.getTransaction().commit();
            session.close();
            return;
        }

        boolean exists = false;
        List<Users> users = session.createQuery("FROM User", Users.class).list();
        for (Users user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            System.out.println("an account with this email already exists");
        } else {
            Users newUser = new Users();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setAge(age);
            newUser.setEmail(email);
            newUser.setPassword(password);
            session.persist(newUser);
            System.out.println("your new account is created. go ahead and login!");
        }

        session.getTransaction().commit();
        session.close();
    }

    private static boolean logIn() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.print("email: ");
        String email = scanner.nextLine().trim().toLowerCase();
        if (!email.contains("@"))
            email = email.concat("@gmail.com");


        System.out.print("password: ");
        String password = scanner.nextLine();

        List<Users> users = session.createQuery("FROM User", Users.class).list();
        for (Users u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                alreadyLoggedin = u;
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }

        System.out.println("wrong email or password.");
        session.getTransaction().commit();
        session.close();
        return false;
    }

}
