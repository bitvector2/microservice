package org.bitvector.test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import io.vertx.core.AbstractVerticle;

public class DBServer extends AbstractVerticle {
    private Cluster cluster;
    private Session session;

    @Override
    public void start() {
        // FIXME
    }

    @Override
    public void stop() {
        // FIXME
    }

    public void connect(String node) {

        vertx.executeBlocking(future -> {
            Cluster c = Cluster.builder()
                    .addContactPoint(node)
                    .build();
            future.complete(c);
        }, res -> {
            cluster = (Cluster) res.result();
        });

        vertx.executeBlocking(future -> {
            Session s = cluster.connect();
            future.complete(s);
        }, res -> {
            session = (Session) res.result();
        });

    }

    public void createSchema() {
        session.executeAsync("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication " +
                "= {'class':'SimpleStrategy', 'replication_factor':1};");

        session.executeAsync(
                "CREATE TABLE IF NOT EXISTS simplex.songs (" +
                        "id uuid PRIMARY KEY," +
                        "title text," +
                        "album text," +
                        "artist text," +
                        "tags set<text>," +
                        "data blob" +
                        ");");
        session.executeAsync(
                "CREATE TABLE IF NOT EXISTS simplex.playlists (" +
                        "id uuid," +
                        "title text," +
                        "album text, " +
                        "artist text," +
                        "song_id uuid," +
                        "PRIMARY KEY (id, title, album, artist)" +
                        ");");
    }

    public void loadData() {
        session.executeAsync(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'," +
                        "{'jazz', '2013'})" +
                        ";");
        session.executeAsync(
                "INSERT INTO simplex.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'" +
                        ");");
    }

    public void querySchema() {
        ResultSet results = session.execute("SELECT * FROM simplex.playlists " +
                "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;");
        System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title", "album", "artist",
                "-------------------------------+-----------------------+--------------------"));
        for (Row row : results) {
            System.out.println(String.format("%-30s\t%-20s\t%-20s", row.getString("title"),
                    row.getString("album"), row.getString("artist")));
        }
        System.out.println();
    }

    public void disconnect() {
        session.closeAsync();
        cluster.closeAsync();
    }

}