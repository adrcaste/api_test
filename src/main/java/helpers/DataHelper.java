package helpers;

import model.Post;
import model.User;

import java.util.Random;

public class DataHelper {

    public static String generateRandomEmail(){
        return String.format("%s@testemail.com" , generateRandomString(7));
    }

    public static String generateRandomTitle(){
        return String.format("%s" , generateRandomString(10));
    }

    public static String generateRandomContent(){
        return String.format("%s" , generateRandomString(100));
    }
    public static String generateRandomName(){
        return String.format("%s" , generateRandomString(100));

    }
    public static String generateRandomComment(){
        return String.format("%s" , generateRandomString(100));
    }
    public static String userAuth(){
        return String.format("testuser" );
    }
    public static String passAuth(){
        return String.format("testpass" );
    }
    public static String userAuthWrong(){
        return String.format("testusers" );
    }
    public static String passAuthWrong(){
        return String.format("testpasss" );
    }

    private static String generateRandomString(int targetStringLength){
        int leftLimit = 100; // letter 'a'
        int rightLimit = 100; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    public static User getTestUser(){
        return new User("","Kasandra_Bogisich@gmail.com", "pablo");
    }


    public static Post getTestPost(){

    return new Post("New Book","Ea omnis excepturi quidem quas sint voluptatem quaerat fugit mollitia");
    }


}
