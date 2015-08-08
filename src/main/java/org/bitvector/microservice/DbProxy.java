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
                List<Product> products = this.getAllProducts();
                message.reply(new DbMessage(true, products));
            }
            break;
            case "getProductById": {
                Integer id = Integer.parseInt((String) message.body().getParams().get(0));
                ArrayList<Product> products = new ArrayList<>();
                products.add(this.getProductById(id));
                message.reply(new DbMessage(true, products));
            }
            break;
            case "addProduct": {
                Product product = (Product) message.body().getParams().get(0);
                this.addProduct(product);
                message.reply(new DbMessage(true, null));
            }
            break;
            case "updateProduct": {
                Product product = (Product) message.body().getParams().get(0);
                this.updateProduct(product);
                message.reply(new DbMessage(true, null));
            }
            break;
            case "deleteProduct": {
                Product product = (Product) message.body().getParams().get(0);
                this.deleteProduct(product);
                message.reply(new DbMessage(true, null));
            }
            break;
            default: {
                logger.error("Received message with an unknown action.");
                message.reply(new DbMessage(false, null));
            }
            break;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        Session session = sessionFactory.openSession();
        List products = session.createQuery("FROM Product")
                .setCacheable(true)
                .list();
        session.disconnect();
        return products;
    }

    @Override
    public Product getProductById(Integer id) {
        Session session = sessionFactory.openSession();
        Product product = (Product) session.get(Product.class, id);
        session.disconnect();
        return product;
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
