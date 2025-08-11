package aut.ap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

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
        Session session = sessionFactory.openSession();





        session.close();
        closeSessionFactory();
    }

}
