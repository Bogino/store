import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Store {

    private static Scanner sc;
    private static boolean isTrue = true;


    public static void main(String[] args) {

        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
        MongoDatabase database = mongoClient.getDatabase("local");
        MongoCollection<Document> shops = database.getCollection("Store");
        MongoCollection<Document> goods = database.getCollection("Goods");

        sc = new Scanner(System.in);

        while (isTrue) {

            System.out.println("Введите команду");

            String command = sc.nextLine().trim();
            String[] array = command.split("\\s+");

            try {
                switch (array[0]) {
                    case "ДОБАВИТЬ_МАГАЗИН":
                        ArrayList<String> goodsList = new ArrayList<>();
                        shops.insertOne(new Document()
                                .append("name", array[1]).append("goods", goodsList));
                        break;
                    case "ДОБАВИТЬ_ТОВАР":
                        goods.insertOne(new Document()
                                .append("name", array[1])
                                .append("price", array[2]));
                        break;
                    case "СТОП":
                        isTrue = false;
                        break;
                    case "ВЫСТАВИТЬ_ТОВАР":
                        Bson query = BsonDocument.parse("{$match: {name: " + array[2] + "}}");
                        Bson query1 = BsonDocument.parse("{ $addFields: { goods: { $concatArrays: [ \"$goods\", [ " + array[1] + " ] ] } } }");
                        List<Bson> queries = new ArrayList<>();
                        queries.add(query);
                        queries.add(query1);
                        shops.aggregate(queries);
                        break;
                    case "СТАТИСТИКА_ТОВАРА":
                        Bson query2 = BsonDocument.parse("${\n" +
                                "     $lookup:\n" +
                                "       {\n" +
                                "         from: \"Goods\",\n" +
                                "         localField: \"goods\",\n" +
                                "         foreignField: \"name\",\n" +
                                "         as: \"goods_in_shop\"\n" +
                                "       }\n" +
                                "  }");
                        List<Bson> queries1 = new ArrayList<>();
                        queries1.add(query2);
                        shops.aggregate(queries1);
                }
            }catch(ArrayIndexOutOfBoundsException ex){
                    ex.getMessage();
                    System.out.println("Некорректно введены данные");
                }
        }
    }
}

