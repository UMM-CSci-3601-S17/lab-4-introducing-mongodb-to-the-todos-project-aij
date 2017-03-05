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

        if (queryParams.containsKey("owner")) {
            String targetOwner = queryParams.get("owner")[0];
            bsonList.add(Aggregates.match(Filters.eq("owner", targetOwner)));
        }

        if (queryParams.containsKey("body")) {
            String targetBody = queryParams.get("body")[0];
            bsonList.add(Aggregates.match(Filters.regex("body", targetBody)));
        }

        if (queryParams.containsKey("category")) {
            String targetCategory = queryParams.get("category")[0];
            bsonList.add(Aggregates.match(Filters.eq("category", targetCategory)));
        }

        if (queryParams.containsKey("status")) {
            String targetStatus = queryParams.get("status")[0];
            bsonList.add(Aggregates.match(Filters.eq("status", Boolean.parseBoolean(targetStatus))));
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

    public float getNumbers(String field, String targetVal) {
        Document document = new Document();
        document.append("status", true);
        document.append(field, targetVal);
        return (float)todoCollection.count(document);
    }

    public String todoSummary() {
        String summary ="";

        float numOfAllTodos = (float)todoCollection.count();
        float numOfCompleteTodos = (float)todoCollection.count(Filters.eq("status", true));

        float completeBarry = getNumbers("owner", "Barry");
        float completeBlanche = getNumbers("owner", "Blanche");
        float completeDawn = getNumbers("owner", "Dawn");
        float completeFry = getNumbers("owner", "Fry");
        float completeRoberta = getNumbers("owner", "Roberta");
        float completeWorkman = getNumbers("owner", "Workman");

        float completeGroceries = getNumbers("category", "groceries");
        float completeHomework = getNumbers("category", "homework");
        float completeSoftwareD = getNumbers("category", "software design");
        float completeVideoGames = getNumbers("category", "video games");
        summary +=
                "Percent-Todos-Complete: " + (numOfCompleteTodos / numOfAllTodos) + "\n" + "\n"
                + "  Categories-Percent-Complete:\n"
                + "    Groceries: " + (completeGroceries / numOfCompleteTodos) + "\n"
                + "    Homework: " + (completeHomework / numOfCompleteTodos) + "\n"
                + "    Software Design: " + (completeSoftwareD / numOfCompleteTodos) + "\n"
                + "    Video Games: " + (completeVideoGames / numOfCompleteTodos) + "\n" + "\n"
                + "  Owners-Percent-Complete:\n"
                + "    Barry: " + (completeBarry / numOfCompleteTodos) + "\n"
                + "    Blanche: " + (completeBlanche / numOfCompleteTodos) + "\n"
                + "    Dawn: " + (completeDawn / numOfCompleteTodos) + "\n"
                + "    Fry: " + (completeFry / numOfCompleteTodos) + "\n"
                + "    Roberta: " + (completeRoberta / numOfCompleteTodos) + "\n"
                + "    Workman: " + (completeWorkman / numOfCompleteTodos) + "\n";

        return summary;
    }

}