package com.example.demo3.data;

import com.example.demo3.Iterator.OrderIterator;
import com.example.demo3.business.Order;
import com.example.demo3.business.Product;
import com.example.demo3.business.ShippingInfo;
import com.example.demo3.business.User;
import javax.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class OrderDAO implements Iterable<Serializable> {
    public static void insert(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.merge(order);
            trans.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
        }
    }
    public static void update(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.merge(order);
            trans.commit();
        } catch (Exception e) {
            System.out.println(e);
            trans.rollback();
        } finally {
            em.close();
        }
    }
    public static double totalIncome() {
        double total=0;
        for (Order o :selectOrder()) {
            total +=o.getOrderDetail().getTotalCost();
        }
        return total;
    }
    public static List<Order> selectOrder() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String qString = "SELECT p from Order p";
        TypedQuery<Order> q = em.createQuery(qString, Order.class);
        List<Order> results = null;
        try {
            results = q.getResultList();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
        return results;
    }
    public static void insertShippingInfo(ShippingInfo shippingInfo){
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(shippingInfo);
            trans.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            em.close();
        }
    }

    public static Order selectOrderByID(int orderID) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        return em.find(Order.class, orderID);
    }

    @Override
    public Iterator<Serializable> iterator() {
        return new OrderIterator(selectOrder());
    }
}
