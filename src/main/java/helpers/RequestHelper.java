package helpers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RequestHelper {

    public static String getUserToken(){
        Response response = given().body(DataHelper.getTestUser()).post("/v1/user/login");

        JsonPath jsonPathEvaluator = response.jsonPath();
        String token = jsonPathEvaluator.get("token.access_token");
        return token;
    }
    public static String getTitlePost(){
        Response response = given().body(DataHelper.getTestPost()).post("/v1/posts");

        JsonPath jsonPathEvaluator = response.jsonPath();
        String title = jsonPathEvaluator.get("title");
        return title;
    }

}
