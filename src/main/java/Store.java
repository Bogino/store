import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import java.util.Arrays;
import java.util.Scanner;


public class Store {


    public static void main(String[] args) {

        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
        MongoDatabase database = mongoClient.getDatabase("local");
        MongoCollection<Document> shops = database.getCollection("Store");
        MongoCollection<Document> goods = database.getCollection("Goods");
        shops.drop();
        goods.drop();

        Scanner sc = new Scanner(System.in);
        boolean isTrue = true;

        while (isTrue) {

            System.out.println("Введите команду");

            String command = sc.nextLine().trim();
            String[] array = command.split("\\s+");

            try {
                switch (array[0]) {
                    case "ДОБАВИТЬ_МАГАЗИН":
                        shops.insertOne(new Document()
                                .append("name", array[1]));
                        break;
                    case "ДОБАВИТЬ_ТОВАР":
                        goods.insertOne(new Document()
                                .append("name", array[1])
                                .append("price", Integer.parseInt(array[2])));
                        break;
                    case "СТОП":
                        isTrue = false;
                        break;
                    case "ВЫСТАВИТЬ_ТОВАР":

                        shops.updateOne(Filters.eq("name", array[2]), Updates.push("goods", array[1]));
                        break;
                    case "СТАТИСТИКА_ТОВАРА":

                        Block<Document> printBlock = document -> System.out.println(document.toJson());
                        shops.aggregate(
                                Arrays.asList(Aggregates.lookup("Goods", "goods", "name", "goodsList"),
                                        Aggregates.unwind("$goodsList"),
                                        Aggregates.group("$name",
                                                Accumulators.min("minPrice","$goodsList.price"),
                                                Accumulators.avg("avgPrice", "$goodsList.price"),
                                                Accumulators.max("maxPrice","$goodsList.price"),
                                                Accumulators.sum("countItems",1))))
                                .forEach(printBlock);

                }
            }catch(ArrayIndexOutOfBoundsException ex){
                    ex.getMessage();
                    System.out.println("Некорректно введены данные");
                }
        }
    }
}

