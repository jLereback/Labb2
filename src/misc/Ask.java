package misc;

import inventory.Product;
import users.Common;

public class Ask {

    public static void forCategoryToUse() {
        System.out.println("""
                                
                Which category would you like to use?
                """);
    }

    public static void forCategoryToRemove() {
        System.out.println("""
                                
                Which category would you like to remove?
                """);
    }

    public static void forProductToEdit() {
        System.out.println("""
                                
                What product would you like to edit?
                """);
    }

    public static void forNewStockOrAmount(String choice, String type) {
        System.out.printf("""
                
                How much would you like to %s the %s?
                """, Common.getIncreaseOrDecrease(choice), type);
    }
    public static void forCategoryToPrint() {
        System.out.println("""
                                
                Would you like to see products from all
                categories or products from a specific one?
                """);
    }

    public static void howManyToAdd(Product tempChosenProduct) {
        System.out.printf("""
                
                How many %s would you like to add?
                """, tempChosenProduct.getName());
    }

    public static void ifLeaving() {
        System.out.print("""
                
                If you leave the shop, the cart will reset
                Do you still want to leave?
                """);
    }
    public static void ifUsingPromoCode() {
        System.out.println("""
                
                Would you like to use a Promo code?""");
    }

    public static void toTryAgain() {
        System.out.println("""
                
                Would you like to try again?""");
    }

    public static void toSaveReceipt() {
        System.out.println("""
                
                Would you like to save the receipt?""");
    }

}
