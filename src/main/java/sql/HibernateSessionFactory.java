package sql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by Quasilin on 09.09.2018.
 */
public class HibernateSessionFactory {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    protected static SessionFactory buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            return new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
            throw new ExceptionInInitializerError("Initial SessionFactory failed");
        }
    }

    public static void init() {
        sessionFactory.openSession();
    }

    public synchronized static Session getSession() {
        return sessionFactory.openSession();
    }

    public static void putSession(Session session){
        session.close();
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
