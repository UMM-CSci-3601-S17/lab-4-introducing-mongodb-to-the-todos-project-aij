package umm3601.todo;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class TodoController {

    private final MongoCollection<Document> todoCollection;

    public TodoController() throws IOException {
        // Set up our server address
        // (Default host: 'localhost', default port:com.mongodb.client.MongoCollection 27017)
        // ServerAddress testAddress = new ServerAddress();

        // Try connecting to the serverwhat is AggregateIterable
        // MongoClient mongoClient = new MongoClient(testAddress, credentials);
        MongoClient mongoClient = new MongoClient(); // Defaults!

        // Try connecting to a database
        MongoDatabase db = mongoClient.getDatabase("test");

        todoCollection = db.getCollection("todos");
    }

    // List todos
    public String listTodos(Map<String, String[]> queryParams) {
        List<Bson> bsonList = new ArrayList<>();

        if (queryParams.containsKey("owner") && queryParams.get("owner")[0] != "") {
            String targetOwner = queryParams.get("owner")[0];
            bsonList.add(Aggregates.match(Filters.eq("owner", targetOwner)));
        }

        if (queryParams.containsKey("status") && queryParams.get("status")[0] != "") {
            String targetStatus = queryParams.get("status")[0];
            bsonList.add(Aggregates.match(Filters.eq("status", Boolean.parseBoolean(targetStatus))));
        }

        if (queryParams.containsKey("body") && queryParams.get("body")[0] != "") {
            String targetBody = queryParams.get("body")[0];
            bsonList.add(Aggregates.match(Filters.regex("body", targetBody)));
        }

        AggregateIterable<Document> matchingTodos = todoCollection.aggregate(bsonList);

        return JSON.serialize(matchingTodos);
    }

    // Get a single to-do
    public String getTodo(String id) {
        FindIterable<Document> jsonTodos
                = todoCollection
                .find(eq("_id", new ObjectId(id)));

        Iterator<Document> iterator = jsonTodos.iterator();

        Document todo = iterator.next();

        return todo.toJson();
    }
}