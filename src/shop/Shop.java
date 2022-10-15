package shop;

import discounts.Discounter;
import json.Json;
import resten.Ask;
import resten.LineUp;
import resten.Print;
import users.Super;
import inventory.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.math.BigDecimal.valueOf;


public abstract class Shop extends Super {

    public static void addToCart
            (Scanner sc, List<Product> products, HashMap<Product, Integer> shoppingCart,
             List<Product> visibleCopyOfProducts) {
        String choice;
        Print.addToCartMenu(visibleCopyOfProducts);
        choice = sc.nextLine().toLowerCase();
        try {
            if (choice.equals("e"))
                Print.goingBackToPreviousMenu();
            else if ((Integer.parseInt(choice) <= products.size())) {
                checkChosenProduct(sc, products, shoppingCart, visibleCopyOfProducts, choice);
            } else
                Print.chooseOneOfTheAlternativesBelow();
        } catch (NumberFormatException e) {
            Print.chooseOneOfTheAlternativesBelow();
        }
    }

    private static void checkChosenProduct
            (Scanner sc, List<Product> products, HashMap<Product, Integer> shoppingCart,
             List<Product> visibleCopyOfProducts, String choice) {
        if (getChosenProduct(visibleCopyOfProducts, choice).getStock() <= 0)
            System.out.println("This product is out of stock");
        else
            addProductToCart(getChosenProduct(visibleCopyOfProducts, choice),
                    sc, shoppingCart, getChosenProduct(products, choice));
    }


    private static Product getChosenProduct(List<Product> products, String choice) {
        return products.get(Integer.parseInt(choice) - 1);
    }

    private static void addProductToCart
            (Product tempChosenProduct, Scanner sc, HashMap<Product, Integer> shoppingCart, Product chosenProduct) {
        Ask.howManyToAdd(tempChosenProduct);
        int amountInCart = getInput(sc);

        if (tempChosenProduct.getStock() < amountInCart)
            addAvailableAmountToCart(tempChosenProduct, shoppingCart, chosenProduct);
        else
            addAmountToCart(tempChosenProduct, shoppingCart, chosenProduct, amountInCart);
    }

    private static void addAvailableAmountToCart
            (Product tempChosenProduct, HashMap<Product, Integer> shoppingCart, Product chosenProduct) {
        int amountInCart;
        Print.addAvailableAmount(tempChosenProduct);
        amountInCart = tempChosenProduct.getStock();
        addAmountToCart(tempChosenProduct, shoppingCart, chosenProduct, amountInCart);
    }


    private static void addAmountToCart
            (Product tempChosenProduct, HashMap<Product, Integer> shoppingCart,
             Product chosenProduct, int amountInCart) {
        if (shoppingCart.containsKey(chosenProduct))
            addAmountToProductInCart(tempChosenProduct, shoppingCart, chosenProduct, amountInCart);
        else
            addNewProductInCart(tempChosenProduct, shoppingCart, chosenProduct, amountInCart);
    }

    private static void addNewProductInCart
            (Product tempChosenProduct, HashMap<Product, Integer> shoppingCart,
             Product chosenProduct, int amountInCart) {
        shoppingCart.put(chosenProduct, amountInCart);
        Print.productAddedToCart(tempChosenProduct, amountInCart);
        updateProductStock(tempChosenProduct, amountInCart);
    }


    private static void addAmountToProductInCart
            (Product tempChosenProduct, HashMap<Product, Integer> shoppingCart,
             Product chosenProduct, int amountInCart) {
        increaseChosenAmount(chosenProduct, shoppingCart.get(chosenProduct), shoppingCart, amountInCart);
        updateProductStock(tempChosenProduct, amountInCart);
    }

    private static void updateProductStock(Product tempChosenProduct, int amountInCart) {
        tempChosenProduct.editStock(tempChosenProduct.getStock() - amountInCart);
    }

    private static int getInput(Scanner sc) {
        return Integer.parseInt(sc.nextLine());
    }

    public static void viewCart(HashMap<Product, Integer> shoppingCart) {
        if (shoppingCart.isEmpty())
            Print.cartIsEmpty();
        else
            Print.cartWithNames(shoppingCart);
    }


    public static void editCart
            (Scanner sc, List<Product> products, HashMap<Product, Integer> shoppingCart,
             List<Product> visibleCopyOfProducts) {
        if (shoppingCart.size() == 0)
            Print.cartIsEmpty();
        else
            editProductInCart(sc, shoppingCart, products, visibleCopyOfProducts);
    }


    private static void editProductInCart
            (Scanner sc, HashMap<Product, Integer> shoppingCart, List<Product> products,
             List<Product> visibleCopyOfProducts) {
        Ask.forProductToEdit();

        Print.cartWithNumbers(shoppingCart, getKeyList(shoppingCart), getValueList(shoppingCart));
        chooseProductInCart(sc.nextLine(), sc, products, shoppingCart,
                getListOfCart(shoppingCart), visibleCopyOfProducts);
    }

    private static List<Product> getListOfCart(HashMap<Product, Integer> shoppingCart) {
        return shoppingCart.keySet().stream().toList();
    }

    private static List<String> getKeyList(HashMap<Product, Integer> shoppingCart) {
        return shoppingCart.keySet().stream().map(Product::printInCart).toList();
    }

    private static List<Integer> getValueList(HashMap<Product, Integer> shoppingCart) {
        return shoppingCart.values().stream().toList();
    }

    private static void chooseProductInCart
            (String choice, Scanner sc, List<Product> products, HashMap<Product, Integer> shoppingCart,
             List<Product> listOfCart, List<Product> visibleCopyOfProducts) {
        if (choice.equals("e"))
            Print.goingBackToPreviousMenu();
        else if (Integer.parseInt(choice) <= products.size()) {
            Product chosenProduct = listOfCart.get(Integer.parseInt(choice) - 1);
            Print.editChosenProduct("amount");
            switchEditProductInCart(sc.nextLine().toLowerCase(), chosenProduct,
                    getTempChosenProduct(visibleCopyOfProducts, chosenProduct),
                    shoppingCart.get(chosenProduct), sc, shoppingCart);
        }
    }

    private static Product getTempChosenProduct(List<Product> visibleCopyOfProducts, Product chosenProduct) {
        for (Product visibleCopyOfProduct : visibleCopyOfProducts) {
            if (visibleCopyOfProduct.getName().equals(chosenProduct.getName())) {
                return visibleCopyOfProduct;
            }
        }
        return null;
    }

    private static void switchEditProductInCart
            (String choice, Product chosenProduct, Product tempChosenProduct,
             int currentAmount, Scanner sc, HashMap<Product, Integer> shoppingCart) {
        switch (choice) {
            case "1" -> increaseItemInCart
                    (sc, choice, chosenProduct, tempChosenProduct, currentAmount, shoppingCart);
            case "2" -> decreaseAmountInCart
                    (sc, choice, chosenProduct, tempChosenProduct, currentAmount, shoppingCart);
            case "e" -> Print.goingBackToPreviousMenu();
            default -> Print.chooseOneOfTheAlternativesBelow();
        }
    }

    private static void increaseItemInCart
            (Scanner sc, String choice, Product chosenProduct, Product tempChosenProduct,
             int currentAmount, HashMap<Product, Integer> shoppingCart) {
        if (tempChosenProduct.getStock() == 0)
            Print.availableAmountAlreadyInCart();
        else {
            increaseSpecifiedProduct(sc, choice, chosenProduct, tempChosenProduct, currentAmount, shoppingCart);
        }
    }

    private static void increaseSpecifiedProduct
            (Scanner sc, String choice, Product chosenProduct, Product tempChosenProduct,
             int currentAmount, HashMap<Product, Integer> shoppingCart) {
        Ask.forNewStockOrAmount(choice, "amount");
        int increasingAmount = getInput(sc);
        if (tempChosenProduct.getStock() < increasingAmount) {
            increaseAvailableAmount(chosenProduct, tempChosenProduct, currentAmount, shoppingCart);
        } else
            increaseChosenAmount(chosenProduct, currentAmount, shoppingCart, increasingAmount);
    }

    private static void increaseChosenAmount
            (Product chosenProduct, int currentAmount, HashMap<Product, Integer> shoppingCart, int increasingAmount) {
        int newAmount = currentAmount + increasingAmount;
        shoppingCart.replace(chosenProduct, currentAmount, newAmount);
    }

    private static void increaseAvailableAmount
            (Product chosenProduct, Product tempChosenProduct, int currentAmount,
             HashMap<Product, Integer> shoppingCart) {
        int availableAmount = tempChosenProduct.getStock();
        int newAmount = currentAmount + availableAmount;
        int emptyStock = 0;
        tempChosenProduct.editStock(emptyStock);
        shoppingCart.replace(chosenProduct, currentAmount, newAmount);
    }

    private static void decreaseAmountInCart
            (Scanner sc, String choice, Product chosenProduct, Product tempChosenProduct,
             int currentAmount, HashMap<Product, Integer> shoppingCart) {
        Ask.forNewStockOrAmount(choice, "amount");
        int decreasingAmount = getInput(sc);
        int newAmount = currentAmount - decreasingAmount;

        if (shoppingCart.get(chosenProduct) - decreasingAmount <= 0) {
            removeProductFromCart(chosenProduct, tempChosenProduct, shoppingCart);
        } else {
            updateProductInCart
                    (chosenProduct, tempChosenProduct, currentAmount, shoppingCart, decreasingAmount, newAmount);
        }
    }

    private static void updateProductInCart
            (Product chosenProduct, Product tempChosenProduct, int currentAmount,
             HashMap<Product, Integer> shoppingCart, int decreasingAmount, int newAmount) {
        shoppingCart.replace(chosenProduct, currentAmount, newAmount);
        tempChosenProduct.editStock(tempChosenProduct.getStock() + decreasingAmount);
        Print.chosenProductUpdated();
    }


    private static void removeProductFromCart
            (Product chosenProduct, Product tempChosenProduct, HashMap<Product, Integer> shoppingCart) {
        tempChosenProduct.editStock(tempChosenProduct.getStock() + shoppingCart.get(chosenProduct));
        shoppingCart.remove(chosenProduct);
        Print.productRemovedFromCart();
    }

    public static void checkOut_Step1(Scanner sc, List<Product> products, HashMap<Product, Integer> shoppingCart, List<Product> visibleCopyOfProducts) {
        viewCart(shoppingCart);
        List<BigDecimal> totalPriceList = new ArrayList<>();
        getListOfTotalPrice(shoppingCart, totalPriceList);

        BigDecimal totalPrice = totalPriceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalPrice = Discounter.applyDiscount(totalPrice);
        if (Objects.equals(finalPrice, totalPrice))
            freeShipping();
        printCheckOutTotalPrice(finalPrice, totalPrice, getTotalNumOfProductsInCart(shoppingCart));

        freeShipping();

        products = visibleCopyOfProducts;
        Json.exportProductsToFile(products);

        receipt();
    }

    //Todo: Om discountern skickade tillbaka pris med rabatt så ska det stå i checkouten att frakten är gratis

    private static void getListOfTotalPrice(HashMap<Product, Integer> shoppingCart, List<BigDecimal> totalPriceList) {
        for (int i = 0; i < getProductPriceList(shoppingCart).size(); i++) {
            totalPriceList.add(getTotalPrice(shoppingCart, i));
        }
    }

    private static BigDecimal getTotalPrice(HashMap<Product, Integer> shoppingCart, int i) {
        return getProductPriceList(shoppingCart).get(i)
                .multiply(getAmountOfEachProductInCart(shoppingCart).get(i));
    }

    public static void receipt() {
        String[] dateAndTime = getTimeAndDate();
        String receipt = """
                
                """ + LineUp.withTab(2) + dateAndTime[0] + LineUp.withTab(2) + dateAndTime[1] +
                """
                
                """;
        Json.exportReceipt(receipt);
    }

    private static String[] getTimeAndDate() {
        String pattern = "yyyy-MM-dd,hh:mm:ss";

        String[] dateAndTime = new SimpleDateFormat(pattern).format(new Date()).split(",");

        System.out.println(LineUp.withTab(2) + dateAndTime[0] + LineUp.withTab(2) + dateAndTime[1]);
        return dateAndTime;
    }


    public static void printCheckOutTotalPrice(BigDecimal finalPrice, BigDecimal totalPrice, BigDecimal totalAmountInCart) {
        var priceSpace = LineUp.price(finalPrice.toString().length());
        BigDecimal shippingCost = valueOf(7);
        Print.newLine();
        productPriceInCart(finalPrice, totalAmountInCart, priceSpace);
        shippingPriceInCart(finalPrice, totalPrice, priceSpace);
        totalPriceInCart(finalPrice, shippingCost, priceSpace);
    }

    public static void shippingPriceInCart(BigDecimal finalPrice, BigDecimal totalPrice, String priceSpace) {
        if (Objects.equals(finalPrice, totalPrice))
            System.out.println("Shipping |" + LineUp.withTab(6) + "| $7");
        else
            System.out.println("Shipping |" + LineUp.withTab(6) + "| Free");
    }

    public static void productPriceInCart(BigDecimal finalPrice, BigDecimal totalAmountInCart, String priceSpace) {
        System.out.println("Products |" + LineUp.withTab(6) + "| $" + finalPrice);
    }

    public static void totalPriceInCart(BigDecimal finalPrice, BigDecimal shippingCost, String priceSpace) {
        System.out.println(LineUp.withTab(6) + "  Total | $" + finalPrice.add(shippingCost));
    }

    private static BigDecimal getTotalNumOfProductsInCart(HashMap<Product, Integer> shoppingCart) {
        return getAmountOfEachProductInCart(shoppingCart).stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static List<BigDecimal> getAmountOfEachProductInCart(HashMap<Product, Integer> shoppingCart) {
        return shoppingCart.values().stream()
                .map(BigDecimal::new).toList();
    }

    private static List<BigDecimal> getProductPriceList(HashMap<Product, Integer> shoppingCart) {
        return shoppingCart.keySet().stream()
                .map(Product::getPrice).toList();
    }

    private static void freeShipping() {

    }

    private BigDecimal getDiscountedPrice(BigDecimal priceToPay) {
        return null;
    }
}