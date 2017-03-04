package umm3601.todo;

import com.mongodb.Block;
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

    Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    /*
    public String todoSummary() {
        AggregateIterable<Document> documents
                = todoCollection.aggregate(
                Arrays.asList(
                        Aggregates.group("$owner",
                                Accumulators.sum("TodosByOwner", 1)),
                        Aggregates.sort(Sorts.ascending("_id"))
                ));
        System.err.println(JSON.serialize(documents));
        return JSON.serialize(documents);
    }

    public String todoSummary() {
        AggregateIterable<Document> documents
                = todoCollection.aggregate(
                Arrays.asList(
                        Aggregates.group("$status",
                                Accumulators.sum("TodosByStatus", 1))
                ));
        System.err.println(JSON.serialize(documents));
        return JSON.serialize(documents);
    }*/

    public float getPercentTodosComplete() {
        float percentTodosComplete;
        float numOfAllTodos;
        float numOfCompleteTodos;

        numOfAllTodos = (float)todoCollection.count();
        numOfCompleteTodos = (float)todoCollection.count(Filters.eq("status", true));

        percentTodosComplete = numOfCompleteTodos / numOfAllTodos;

        return percentTodosComplete;
    }

    public float getNumbers(String field, String targetVal) {
        Document document = new Document();
        document.append("status", true);
        document.append(field, targetVal);
        return (float)todoCollection.count(document);
    }

    public String todoSummary() {
        String summary ="";

        //float numOfAllTodos = (float)todoCollection.count();
        //float numOfCompleteTodos = (float)todoCollection.count(Filters.eq("status", true));

        float completeBarry = getNumbers("owner", "Barry");
        float completeBlanche = getNumbers("owner", "Blanche");
        float completeDawn = getNumbers("owner", "Dawn");
        float completeFry = getNumbers("owner", "Fry");
        float completeRoberta = getNumbers("owner", "Roberta");
        float completeWorkman = getNumbers("owner", "Workman");
        System.out.println("1: " + completeBarry);
        System.out.println("2: " + completeBlanche);
        System.out.println("3: " + completeDawn);
        System.out.println("4: " + completeFry);
        System.out.println("5: " + completeRoberta);
        System.out.println("6: " + completeWorkman);

        float completeGroceries = getNumbers("owner", "Barry");
        float completeHomework = getNumbers("owner", "Blanche");
        float completeSoftwareD = getNumbers("owner", "Dawn");
        float completeVideoGames = getNumbers("owner", "Fry");
        System.out.println("7: " + completeGroceries);
        System.out.println("8: " + completeHomework);
        System.out.println("9: " + completeSoftwareD);
        System.out.println("10: " + completeVideoGames);

        summary +=
                "Percent-Todos-Complete: " + getPercentTodosComplete() + "\n" + "\n"
                + "  Categories-Percent-Complete:\n"
                + "    Groceries: " + "\n"
                + "    Homework: " + "\n"
                + "    Software Design: " + "\n"
                + "    Video Games: " + "\n" + "\n"
                + "  Owners-Percent-Complete:\n"
                + "    Barry: " + "\n"
                + "    Blanche: " + "\n"
                + "    Dawn: " + "\n"
                + "    Fry: " + "\n"
                + "    Roberta: " + "\n"
                + "    Workman: " + "\n";

        return summary;
    }

}