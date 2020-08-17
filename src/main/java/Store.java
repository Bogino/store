import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;


public class Store {


    public static void main(String[] args) {

        MongoMarket market = new MongoMarket();

        Scanner sc = new Scanner(System.in);
        boolean isTrue = true;

        while (isTrue) {

            System.out.println("Введите команду");

            String command = sc.nextLine().trim();
            String[] array = command.split("\\s+");

            try {
                switch (array[0]) {
                    case "ДОБАВИТЬ_МАГАЗИН":
                        market.addMarket(array[1]);
                        break;
                    case "ДОБАВИТЬ_ТОВАР":
                        market.addItem(array[1], array[2]);
                        break;
                    case "СТОП":
                        isTrue = false;
                        break;
                    case "ВЫСТАВИТЬ_ТОВАР":
                        market.submit(array[2], array[1]);
                        break;
                    case "СТАТИСТИКА_ТОВАРА":

                       market.getStatistics();
                       break;

                }
            }catch(ArrayIndexOutOfBoundsException ex){
                    ex.getMessage();
                    System.out.println("Некорректно введены данные");
                }
        }
    }
}

