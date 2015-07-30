package org.bitvector.microservice_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    private ObjectMapper jsonMapper;

    @Override
    public void start() {
        logger = LoggerFactory.getLogger("org.bitvector.microservice_test.DbPersister");

        Configuration configuration = new Configuration()
                .setProperties(new Properties(System.getProperties()))
                .addAnnotatedClass(Product.class);                                  // SUPER FUCKING IMPORTANT
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        EventBus eb = vertx.eventBus();
        eb.consumer("DbPersister", this::onMessage);
        DbMessageCodec dbMessageCodec = new DbMessageCodec();
        eb.registerDefaultCodec(DbMessage.class, dbMessageCodec);

        jsonMapper = new ObjectMapper();

        logger.info("Started a DbPersister...");
    }

    @Override
    public void stop() {
        sessionFactory.close();
        logger.info("Stopped a DbPersister...");
    }

    private void onMessage(Message<DbMessage> message) {
        switch (message.body().getAction()) {
            case "handleGetAllProducts":
                this.handleGetAllProducts(message);
                break;
            case "handleGetProductId":
                this.handleGetProductId(message);
                break;
            default:
                logger.error("Received message with an unknown action.");
                break;
        }
    }

    private void handleGetAllProducts(Message<DbMessage> message) {
        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product").list();
        session.close();

        List<Product> products = new ArrayList<>();
        for (Object obj : objs) {
            Product product = (Product) obj;
            products.add(product);
        }

        try {
            String jsonString = jsonMapper.writeValueAsString(products);
            DbMessage dbResponse = new DbMessage(jsonString);
            message.reply(dbResponse);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert Products to JSON", e);
        }
    }

    private void handleGetProductId(Message<DbMessage> message) {
        Integer productID = Integer.parseInt(message.body().getParams());

        Session session = sessionFactory.openSession();
        List objs = session.createQuery("FROM Product WHERE id=:id")
                .setParameter("id", productID)
                .list();
        session.close();

        List<Product> products = new ArrayList<>();
        for (Object obj : objs) {
            Product product = (Product) obj;
            products.add(product);
        }

        try {
            String jsonString = jsonMapper.writeValueAsString(products);
            DbMessage dbResponse = new DbMessage(jsonString);
            message.reply(dbResponse);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert a Product to JSON", e);
        }
    }

    private void handlePutProductId(Message<DbMessage> message) {
        // FIXME
    }

    private void handlePostProduct(Message<DbMessage> message) {
        // FIXME
    }

    private void handleDeleteProductId(Message<DbMessage> message) {
        // FIXME
    }
}
