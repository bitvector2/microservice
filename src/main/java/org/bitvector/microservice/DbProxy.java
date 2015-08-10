package org.bitvector.microservice;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DbProxy extends AbstractVerticle implements ProductDAO {

    private Logger logger;
    private SessionFactory sessionFactory;


    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice.DbProxy");

        EventBus eb = vertx.eventBus();
        eb.consumer("DbProxy", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        Configuration configuration = new Configuration()
                .setProperties(new Properties(System.getProperties()))
                .addAnnotatedClass(Product.class)                         // SUPER FUCKING IMPORTANT PER COLLECTION
                .configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        logger.info("Started a DbProxy...");
    }

    @Override
    public void stop() {
        sessionFactory.close();
        logger.info("Stopped a DbProxy...");
    }

    private void onMessage(Message<DbMessage> message) {
        switch (message.body().getAction()) {
            case "getAllProducts": {
                message.reply(new DbMessage(true, this.getAllProducts()));
            }
            break;
            case "getProductById": {
                Integer id = (Integer) message.body().getParam();
                message.reply(new DbMessage(true, this.getProductById(id)));
            }
            break;
            case "addProduct": {
                Product product = (Product) message.body().getParam();
                this.addProduct(product);
                message.reply(new DbMessage(true));
            }
            break;
            case "updateProduct": {
                Product product = (Product) message.body().getParam();
                this.updateProduct(product);
                message.reply(new DbMessage(true));
            }
            break;
            case "deleteProduct": {
                Product product = (Product) message.body().getParam();
                this.deleteProduct(product);
                message.reply(new DbMessage(true));
            }
            break;
            default: {
                logger.error("Received message with an unknown action.");
                message.reply(new DbMessage(false));
            }
            break;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product")
                .setCacheable(true)
                .list();
        session.disconnect();

        List<Product> products = new ArrayList<>();
        for (Object obj : objs) {
            products.add((Product) obj);
        }
        return products;
    }

    @Override
    public Product getProductById(Integer id) {
        Session session = sessionFactory.openSession();
        List products = session.createQuery("FROM Product WHERE id=:id")
                .setParameter("id", id)
                .setCacheable(true)
                .list();
        session.disconnect();
        return (Product) products.get(0);
    }

    @Override
    public void addProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(product);
        tx.commit();
        session.disconnect();
    }

    @Override
    public void updateProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(product);
        tx.commit();
        session.disconnect();
    }

    @Override
    public void deleteProduct(Product product) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.delete(product);
        tx.commit();
        session.disconnect();
    }
}
