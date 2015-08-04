package org.bitvector.microservice_test;


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


public class DbPersister extends AbstractVerticle {

    private Logger logger;
    private SessionFactory sessionFactory;


    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        Configuration configuration = new Configuration()
                .setProperties(new Properties(System.getProperties()))
                .addAnnotatedClass(Product.class)                                  // SUPER FUCKING IMPORTANT
                .configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        logger.info("Started a DbPersister...");
    }

    @Override
    public void stop() {
        sessionFactory.close();
        logger.info("Stopped a DbPersister...");
    }

    private void onMessage(Message<DbMessage> message) {
        switch (message.body().getAction()) {
            case "handlePing":
                this.handlePing(message);
                break;
            case "handleGetAllProducts":
                this.handleGetAllProducts(message);
                break;
            case "handleGetProductId":
                this.handleGetProductId(message);
                break;
            case "handlePutProductId":
                this.handlePutProductId(message);
                break;
            case "handlePostProduct":
                this.handlePostProduct(message);
                break;
            case "handleDeleteProductId":
                this.handleDeleteProductId(message);
                break;
            default:
                logger.error("Received message with an unknown action.");
                message.reply(new DbMessage(false, null));
                break;
        }
    }

    private void handlePing(Message<DbMessage> message) {
        Session session = sessionFactory.openSession();
        message.reply(new DbMessage(true, null));
        session.disconnect();
    }

    private void handleGetAllProducts(Message<DbMessage> message) {
        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product")
                .setCacheable(true)
                .list();
        session.disconnect();
        message.reply(new DbMessage(true, objs));
    }

    private void handleGetProductId(Message<DbMessage> message) {
        String id = (String) message.body().getParams().get(0);
        ArrayList<Product> objs = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Product product = (Product) session.get(Product.class, Integer.parseInt(id));
        objs.add(product);
        session.disconnect();
        message.reply(new DbMessage(true, objs));
    }

    private void handlePutProductId(Message<DbMessage> message) {
        Product product = (Product) message.body().getParams().get(0);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(product);
        tx.commit();
        session.disconnect();
        message.reply(new DbMessage(true, null));
    }

    private void handlePostProduct(Message<DbMessage> message) {
        Product product = (Product) message.body().getParams().get(0);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(product);
        tx.commit();
        session.disconnect();
        message.reply(new DbMessage(true, null));
    }

    private void handleDeleteProductId(Message<DbMessage> message) {
        String id = (String) message.body().getParams().get(0);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Product product = (Product) session.get(Product.class, Integer.parseInt(id));
        session.delete(product);
        tx.commit();
        session.disconnect();
        message.reply(new DbMessage(true, null));
    }
}
