package com.example.demo3.data;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


// singleton design pattern
public class DBUtil {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("JPAConnection");

    public static EntityManagerFactory getEmFactory() {
        return emf;
    }

    /*public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://us-cdbr-east-06.cleardb.net:3306/heroku_f382b427d1d8f28?useSSL=false");
                settings.put(Environment.USER, "b45b05219db5fd");
                settings.put(Environment.PASS, "72f39aa8");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");

                settings.put(Environment.SHOW_SQL, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                settings.put(Environment.HBM2DDL_AUTO, "update");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Account.class);
                configuration.addAnnotatedClass(Product.class);
                configuration.addAnnotatedClass(LineItem.class);
                configuration.addAnnotatedClass(Cart.class);
                configuration.addAnnotatedClass(ShippingInfo.class);
                configuration.addAnnotatedClass(Order.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                System.out.println("Hibernate Java Config serviceRegistry created");
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                return sessionFactory;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }*/
}
