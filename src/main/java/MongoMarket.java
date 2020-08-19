import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.Arrays;
import java.util.function.Consumer;

public class MongoMarket {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> markets;
    private MongoCollection<Document> goods;

    public MongoMarket(){

        mongoClient = new MongoClient("127.0.0.1", 27017);
        database = mongoClient.getDatabase("local");
        markets = database.getCollection("Market");
        goods = database.getCollection("Goods");
        markets.drop();
        goods.drop();
    }

    public void addMarket(String name){
        markets.insertOne(new Document()
                .append("name", name));
    }

    public void addItem(String name, String price){
        goods.insertOne(new Document()
                .append("name", name)
                .append("price", Integer.parseInt(price)));
    }

    public void submit(String marketName, String itemName){
        try {

            markets.find(Filters.eq("name", marketName)).first().equals(null);

        } catch (NullPointerException ex){
            System.out.println("Такого магазина нет(");
        }

        try {

            goods.find(Filters.eq("name", itemName)).first().equals(null);

        } catch (NullPointerException ex){
            System.out.println("Нет такого товара(");
        }
        markets.updateOne(Filters.eq("name", marketName), Updates.push("goods", itemName));

    }

    public void getStatistics(){
        markets.aggregate(
                Arrays.asList(Aggregates.lookup("Goods", "goods", "name", "goodsList"),
                        Aggregates.unwind("$goodsList"),
                        Aggregates.group("$name",
                                Accumulators.min("minPrice","$goodsList.price"),
                                Accumulators.avg("avgPrice", "$goodsList.price"),
                                Accumulators.max("maxPrice","$goodsList.price"),
                                Accumulators.sum("countItems",1),
                                Accumulators.sum("sub100", new BasicDBObject("$cond", Arrays.asList(new BasicDBObject("$lt",
                                        Arrays.asList("$goodsList.price",100)), 1, 0))))))
                .forEach((Consumer<? super Document>) System.out::println);
    }

}
