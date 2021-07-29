package sql;

import org.hibernate.Session;

import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by Quasilin on 09.09.2018.
 */
public class Hibernator {
    private static final Hibernator instance = new Hibernator();

    public static synchronized Hibernator getInstance() {
        return instance;
    }

    public void delete(Object object){
        Session session = HibernateSessionFactory.getSession();
        session.delete(object);
        session.beginTransaction().commit();
        HibernateSessionFactory.putSession(session);
    }

    public <T>List<T> limitQuery(Class<T> tClass, HashMap<String, Object> parameters, int limit) {
        Session session = HibernateSessionFactory.getSession();
        CriteriaQuery<T> query = getCriteriaQuery(session, tClass, parameters);

        List<T> resultList = session.createQuery(query)
                .setMaxResults(limit)
                .getResultList();

        HibernateSessionFactory.putSession(session);

        return resultList;
    }
    public <T>List<T> limitQuery(Class<T> tClass, String parameter, Object value, int limit) {
        final HashMap<String, Object> param = new HashMap<>();
        param.put(parameter, value);
        return limitQuery(tClass, param, limit);
    }

    public static final String SLASH = "/";

    <K, T> Predicate[] buildPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<K> root, HashMap<String, Object> param){
        if (param != null){
            Predicate[] predicates = new Predicate[param.size()];
            int i = 0;

            for (Map.Entry<String, Object> entry : param.entrySet()){

                Path<Date> path = parsePath(root, entry.getKey());

                if (entry.getValue() == null){
                    predicates[i] = criteriaBuilder.isNull(path);
                } else  {
                    predicates[i] = criteriaBuilder.equal(path, entry.getValue());
                }
                i++;
            }
            return predicates;
        }
        return new Predicate[0];
    }

    private <T, K> Root<K> buildQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Class<K> tClass, HashMap<String, Object> param){
        Root<K> from = query.from(tClass);
        query.where(buildPredicates(criteriaBuilder, query, from, param));
        return from;
    }

    private <T> CriteriaQuery<T> getCriteriaQuery(Session session, Class<T> tClass, HashMap<String, Object> parameters) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        return getCriteriaQuery(criteriaBuilder, tClass, parameters);
    }

    private <T> CriteriaQuery<T> getCriteriaQuery(CriteriaBuilder builder, Class<T> tClass, HashMap<String, Object> parameters) {
        CriteriaQuery<T> query = builder.createQuery(tClass);

        buildQuery(builder, query, tClass, parameters);

        return query;
    }

    private <T> Path<Date> parsePath(Root<T> root, String value){
        Path<Date> objectPath = null;
        String[] split = value.split(SLASH);

        for (String s : split){
            if (objectPath == null) {
                objectPath = root.get(s);
            } else {
                objectPath = objectPath.get(s);
            }
        }

        return objectPath;
    }

    public synchronized <T>List<T> query(Class<T> tClass, HashMap<String, Object> params){
        Session session = HibernateSessionFactory.getSession();
        CriteriaQuery<T> query = getCriteriaQuery(session, tClass, params);

        List<T> resultList = session.createQuery(query).getResultList();

        HibernateSessionFactory.putSession(session);
        putParams(params);
        return resultList;
    }

    private void putParams(HashMap<String, Object> params) {
        if (params != null) {
            params = new HashMap<>();
            pool.add(params);
        }
    }

    public <T> List<T> query(Class<T> tClass, String key, Object value) {
        HashMap<String, Object> params = getParams();
        params.put(key, value);
        return query(tClass, params);
    }

    final ArrayList<HashMap<String, Object>> pool = new ArrayList<>();
    synchronized HashMap<String, Object> getParams(){
        if (pool.size() > 0){
            return pool.remove(0);
        } else {
            return new HashMap<>();
        }
    }

    public <T>T get(Class<T> tClass, String key, Object value){
        HashMap<String, Object> params = getParams();
        params.put(key, value);
        return get(tClass, params);
    }

    public <T> T get(Class<T> tClass, HashMap<String, Object> parameters) {

        List<T> query = query(tClass, parameters);
        if (query == null || query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public <T> void removeList(List<T> list) {
        Session session = HibernateSessionFactory.getSession();
        list.forEach(session::delete);
        session.beginTransaction().commit();
        HibernateSessionFactory.putSession(session);
    }

    public void save(Object ... objects) {
        Session session = HibernateSessionFactory.getSession();
        for (Object o : objects){
            session.saveOrUpdate(o);
        }
        session.beginTransaction().commit();
        HibernateSessionFactory.putSession(session);
    }

}
