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
                "                    owner: \"Blanche\",\n" +
                "                    _id: \"58895985186754887e0381f5\",\n" +
                "                    status: true,\n" +
                "                    body: \"Incididunt enim ea sit qui esse magna eu. Nisi sunt exercitation est Lorem consectetur incididunt cupidatat laboris commodo veniam do ut sint.\"\n" +
                "                    category: \"software design\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    owner: \"Barry\",\n" +
                "                    _id: \"588959856f0b82ee93cd93eb\",\n" +
                "                    status: true,\n" +
                "                    body: \"Nisi sit non non sunt veniam pariatur. Elit reprehenderit aliqua consectetur est dolor officia et adipisicing elit officia nisi elit enim nisi.\"\n" +
                "                    category: \"video games\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    owner: \"Fry\",\n" +
                "                    _id: \"588959856601f6a77b6a2862\",\n" +
                "                    status: false,\n" +
                "                    body: \"Sunt esse dolore sunt Lorem velit reprehenderit incididunt minim Lorem sint Lorem sit voluptate proident. Veniam voluptate veniam aliqua ipsum cupidatat.\"\n" +
                "                    category: \"homework\"\n" +
                "                }"));
        ObjectId samsId = new ObjectId();
        BasicDBObject sam = new BasicDBObject("_id", samsId);
        sam = sam.append("owner", "Sam")
                .append("_id", "5889598509902931d")
                .append("status", false)
                .append("body", "Magna exercitation pariatur in labore. Voluptate adipisicing reprehenderit dolor veniam dolore amet duis anim nisi.")
                .append("category", "software design");
        samsIdString = samsId.toHexString();
        todoDocuments.insertMany(testTodos);
        todoDocuments.insertOne(Document.parse(sam.toJson()));

        // It might be important to construct this _after_ the DB is set up
        // in case there are bits in the constructor that care about the state
        // of the database.
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

    private static String getName(BsonValue val) {
        BsonDocument doc = val.asDocument();
        return ((BsonString) doc.get("owner")).getValue();
    }

    @Test
    public void getAllTodos() {
        Map<String, String[]> emptyMap = new HashMap<>();
        String jsonResult = todoController.listTodos(emptyMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 4 todos", 4, docs.size());
        List<String> names = docs
                .stream()
                .map(TodoControllerSpec::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Fry", "Blanche", "Dawn", "Roberta");
        assertEquals("Names should match", expectedNames, names);
    }

    @Test
    public void getTodosWhoAre37() {
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("_id", new String[] { "588959852a278361a5ea251a" });
        String jsonResult = todoController.listTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 1 todo", 1, docs.size());
        List<String> names = docs
                .stream()
                .map(TodoControllerSpec::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Workman", "Fry");
        assertEquals("Names should match", expectedNames, names);
    }

    @Test
    public void getSamById() {
        String jsonResult = todoController.getTodo(samsIdString);
        Document sam = Document.parse(jsonResult);
        assertEquals("Name should match", "Barry", sam.get("owner"));
    }
}
