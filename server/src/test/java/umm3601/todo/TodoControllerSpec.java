package umm3601.todo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for the UserController.
 *
 * Created by mcphee on 22/2/17.
 */
public class TodoControllerSpec
{
    private TodoController todoController;
    private String samsIdString;

    @Before
    public void clearAndPopulateDB() throws IOException {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection<Document> todoDocuments = db.getCollection("todos");
        todoDocuments.drop();
        List<Document> testTodos = new ArrayList<>();
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"BarryId\",\n" +
                "                    owner: \"Barry\",\n" +
                "                    status: true,\n" +
                "                    category: \"homework\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"BlancheId\",\n" +
                "                    owner: \"Blanche\",\n" +
                "                    status: true,\n" +
                "                    category: \"homework\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"FryId\",\n" +
                "                    owner: \"Fry\",\n" +
                "                    status: false,\n" +
                "                    category: \"groceries\"\n" +
                "                }"));

        ObjectId samsId = new ObjectId();
        BasicDBObject sam = new BasicDBObject("_id", samsId);
        sam = sam.append("owner", "Sam")
                .append("status", true)
                .append("category", "groceries");
        samsIdString = samsId.toHexString();
        todoDocuments.insertMany(testTodos);
        todoDocuments.insertOne(Document.parse(sam.toJson()));

        todoController = new TodoController();
    }

    // http://stackoverflow.com/questions/34436952/json-parse-equivalent-in-mongo-driver-3-x-for-java
    private BsonArray parseJsonArray(String json) {
        final CodecRegistry codecRegistry
                = CodecRegistries.fromProviders(Arrays.asList(
                new ValueCodecProvider(),
                new BsonValueCodecProvider(),
                new DocumentCodecProvider()));

        JsonReader reader = new JsonReader(json);
        BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);

        return arrayReader.decode(reader, DecoderContext.builder().build());
    }

    private static String getOwner(BsonValue val) {
        BsonDocument doc = val.asDocument();
        return ((BsonString) doc.get("owner")).getValue();
    }

    @Test
    public void getAllTodos() {
        Map<String, String[]> emptyMap = new HashMap<>();
        String jsonResult = todoController.listTodos(emptyMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 4 todos", 4, docs.size());
        List<String> owners = docs
                .stream()
                .map(TodoControllerSpec::getOwner)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Barry", "Blanche", "Fry", "Sam");
        assertEquals("Owners should match", expectedOwners, owners);
    }

    @Test
    public void getCompleteTodos() {
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("category", new String[] { "homework" });
        String jsonResult = todoController.listTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 2 todos", 2, docs.size());
        List<String> owners = docs
                .stream()
                .map(TodoControllerSpec::getOwner)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Barry", "Blanche");
        assertEquals("Owners should match", expectedOwners, owners);
    }

    @Test
    public void getSamById() {
        String jsonResult = todoController.getTodo(samsIdString);
        Document sam = Document.parse(jsonResult);
        assertEquals("Owner should match", "Sam", sam.get("owner"));
    }
}

