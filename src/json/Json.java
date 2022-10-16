package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import inventory.Category;
import inventory.Product;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Json {
    static Path productPath = Path.of("src/Json/products.json");
    static Path categoryPath = Path.of("src/Json/categories.json");
    static Path receiptPath = Path.of("src/Json/receipt.txt");

    public static void exportProductsToFile(List<Product> products) {
        Gson gson = new Gson();

        String json = gson.toJson(products);

        try {
            Files.writeString(productPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Product> importProductsFromFile() throws IOException {

        return new Gson().fromJson(Files.readString(productPath), new TypeToken<ArrayList<Product>>() {
        }.getType());
    }

    public static void exportCategoryListToFile(List<Category> categoryList) {
        Gson gson = new Gson();

        String json = gson.toJson(categoryList);

        try {
            Files.writeString(categoryPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Category> importCategoryListFromFile() throws IOException {

        return new Gson().fromJson(Files.readString(categoryPath), new TypeToken<ArrayList<Category>>() {
        }.getType());
    }

    public static void exportReceipt(String receipt) {
        try {
            Files.writeString(receiptPath, receipt, StandardOpenOption.WRITE);
            String fileContent = Files.readString(receiptPath);
            System.out.println(fileContent);

        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}

