package aut.ap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static void showUnreadEmails() {
        Session session = sessionFactory.openSession();
        List<Emails> emails = session.createQuery("FROM Emails WHERE recipient = :email AND isRead = false ORDER BY id DESC", Emails.class)
                .setParameter("email", alreadyLoggedin.getEmail())
                .list();

        System.out.println(emails.size() + "unread email(s):");
        for (Emails e : emails) {
            System.out.println("code: " + e.getCode() + ", sender: " + e.getSender() + ", subject: " + e.getSubject());
        }

        session.close();
    }

    private static void sendEmail() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<String> recipients = new ArrayList<>();
        System.out.println("enter recipient emails one by one. type 'done' when finished:");

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            if (!input.contains("@")) {
                input = input.concat("@milou.com");
            }

            recipients.add(input);
        }

        System.out.print("subject: ");
        String subject = scanner.nextLine();
        System.out.print("body: ");
        String body = scanner.nextLine();

        for (String recipientEmail : recipients) {
            Emails email = new Emails();
            email.setSender(alreadyLoggedin.getEmail());
            email.setRecipient(recipientEmail);
            email.setSubject(subject);
            email.setBody(body);
            email.setRead(false);
            email.setTimestamp(LocalDate.now());

            String code = generateCode();
            email.setCode(code);

            session.persist(email);

            System.out.println("successfully sent your email.");
            System.out.println("code: " + code);
        }

        session.getTransaction().commit();
        session.close();
    }

    private static void viewEmails() {
        System.out.println("[a]ll, [u]nread, [s]ent, Read by [c]ode:");
        String option = scanner.nextLine().trim().toLowerCase();

        Session session = sessionFactory.openSession();
        List<Emails> emails = null;

        switch (option) {
            case "a":
                emails = session.createQuery("FROM Emails WHERE recipient = :email OR sender = :email ORDER BY id DESC", Emails.class)
                        .setParameter("email", alreadyLoggedin.getEmail()).list();
                break;
            case "u":
                emails = session.createQuery("FROM Emails WHERE recipient = :email AND isRead = false ORDER BY id DESC", Emails.class)
                        .setParameter("email", alreadyLoggedin.getEmail()).list();
                break;
            case "s":
                emails = session.createQuery("FROM Emails WHERE sender = :email ORDER BY id DESC", Emails.class)
                        .setParameter("email", alreadyLoggedin.getEmail()).list();
                break;
            case "c":
                System.out.print("enter email code: ");
                String code = scanner.nextLine().trim();
                Emails email = session.createQuery("FROM Emails WHERE code = :code", Emails.class)
                        .setParameter("code", code)
                        .uniqueResult();

                if (email != null && (email.getSender().equals(alreadyLoggedin.getEmail()) || email.getRecipient().equals(alreadyLoggedin.getEmail()))) {
                    System.out.println("subject: " + email.getSubject());
                    System.out.println("body: " + email.getBody());
                    email.setRead(true);
                } else {
                    System.out.println("you cannot read this email.");
                }
                session.beginTransaction();
                session.getTransaction().commit();
                session.close();
                return;
        }

        if (emails != null) {
            for (Emails e : emails) {
                System.out.println("code: " + e.getCode() + ", sender: " + e.getSender() + ", recipient: " + e.getRecipient() + ", subject: " + e.getSubject());
            }
        }
        session.close();
    }

    private static void replyToEmail() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.print("enter email code to reply: ");
        String code = scanner.nextLine().trim();
        List<Emails> results = session.createQuery("FROM Emails WHERE code = :code", Emails.class)
                .setParameter("code", code)
                .getResultList();

        Emails original = null;
        for (Emails e : results) {
            original = e;
            break;
        }


        if (original != null && original.getRecipient().equalsIgnoreCase(alreadyLoggedin.getEmail())) {
            System.out.print("body: ");
            String body = scanner.nextLine();

            Emails reply = new Emails();
            reply.setSender(alreadyLoggedin.getEmail());
            reply.setRecipient(original.getSender());
            reply.setSubject("Re: " + original.getSubject());
            reply.setBody(body);
            reply.setRead(false);
            reply.setCode(generateCode());
            session.persist(reply);

            System.out.println("successfully sent your reply to email " + code + ".\nCode: " + reply.getCode());
        } else {
            System.out.println("you cannot reply to this email.");
        }

        session.getTransaction().commit();
        session.close();
    }

    private static void forwardEmail() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        System.out.print("enter email code to forward: ");
        String code = scanner.nextLine().trim();
        List<Emails> results = session.createQuery("FROM Emails WHERE code = :code", Emails.class)
                .setParameter("code", code)
                .getResultList();

        Emails original = null;
        for (Emails e : results) {
            original = e;
            break;
        }


        if (original != null && (original.getRecipient().equalsIgnoreCase(alreadyLoggedin.getEmail()) || original.getSender().equalsIgnoreCase(alreadyLoggedin.getEmail()))) {
            List<String> recipients = new ArrayList<>();
            System.out.println("enter recipient emails one by one. type 'done' when finished:");

            while (true) {
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("done")) {
                    break;
                }

                if (!input.contains("@")) {
                    input = input.concat("@milou.com");
                }

                recipients.add(input);
            }

            for (String r : recipients) {
                Emails forward = new Emails();
                forward.setSender(alreadyLoggedin.getEmail());
                forward.setRecipient(r.trim().toLowerCase());
                forward.setSubject("Fwd: " + original.getSubject());
                forward.setBody(original.getBody());
                forward.setRead(false);
                forward.setCode(generateCode());
                session.persist(forward);
            }

            System.out.println("successfully forwarded your email.\ncode: " + generateCode());
        } else {
            System.out.println("you cannot forward this email.");
        }

        session.getTransaction().commit();
        session.close();
    }

    private static String generateCode() {
        String letters = "abcdefghijklmnopqrstuvwxyz0123456789";
        String code = "";
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(letters.length());
            code = code.concat(String.valueOf(letters.charAt(index)));
        }
        return code;
    }


}
