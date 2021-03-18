import helpers.DataHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Post;
import org.hamcrest.core.IsEqual;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import specifications.ResponseSpecs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class PostTests extends BaseTest {
    private static String resourcePath = "/v1/post";
    private static String resourcePathGet = "/v1/posts";
    private static Integer createdPost = 0;

    @BeforeGroups("create_post")
    public void Test_Create_Post() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());
        Response response = given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .post(resourcePath);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdPost = jsonPathEvaluator.get("id");
    }

    @Test (priority=1)
    public void Test_Create_Post_Success() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .post(resourcePath)
                .then()
                .statusCode(200)
                .and()
                .body("message", IsEqual.equalTo("Post created"))
                .spec(ResponseSpecs.defaultSpec());
    }


    @Test (priority=2)
    public void Test_Get_All_Post_Success() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .get(resourcePathGet)
                .then()
                .statusCode(200)
                .and()
                .body(containsString("total"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("email"))
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=3)

    public void Test_Get_One_Post_Success() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .get(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=4)
    public void Test_Update_Post_Success() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

       given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .put(resourcePath + "/" + createdPost.toString())
                .then()
                .body("message",equalTo("Post updated"))
                .and()
                .statusCode(200)
               .and()
               .body("message", IsEqual.equalTo("Post updated"))
                .spec(ResponseSpecs.defaultSpec());

    }

    @Test (groups = "create_post",priority=5)
    public void Test_Delete_Post_Success() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(200)
                .and()
                .body("message", IsEqual.equalTo("Post deleted"))
                .spec(ResponseSpecs.defaultSpec());

    }

    ///////********************* Negative Testing ******************************///////////////////////////
    @Test   (priority=6)
    public void Test_Create_Post_MandatoryField_Fail() {

        Post postTest = new Post("","222222");

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .post(resourcePath)
                .then()
                .statusCode(406)
                .and()
                .spec(ResponseSpecs.defaultSpec());
    }


    @Test(priority=7)
    public void Test_Get_All_Post_Fail() {

        Post postTest = new Post("","");

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .get(resourcePathGet)
                .then()
                .statusCode(401)
                .and()
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=8)

    public void Test_Get_One_Post_doesnt_exist() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .get(resourcePath + "/899999999999")
                .then()
                .body(containsString("error"))
                .body("Message",equalTo("Post not found"))
                .statusCode(404)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=9)
    public void Test_Update_Post_doesnt_exist_Fail() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .put(resourcePath + "/7999999999" )
                .then()
                .statusCode(406)
                .body("error",equalTo("Post not found"))
                .body("message",equalTo("Post could not be updated"))
                .spec(ResponseSpecs.defaultSpec());

    }

    @Test (groups = "create_post",priority=10)
    public void Test_Delete_Post_doesnt_Exist_Fail() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateToken())
                .body(postTest)
                .delete(resourcePath + "/89000087777777")
                .then()
                .statusCode(406)
                .and()
                .body("error",equalTo("Post not found"))
                .body("message",equalTo("Post could not be deleted"))
                .spec(ResponseSpecs.defaultSpec());

    }


    /////////////////////***********Test Security**********************/////////////////////////////////////
    @Test(priority=11)
    public void Test_Create_Post_NotValidAuth() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .post(resourcePath)
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }


    @Test (priority=12)
    public void Test_Get_All_Post_NotValidAuth() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .get(resourcePathGet)
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=13)

    public void Test_Get_One_Post_NotValidAuth() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .get(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test (groups = "create_post",priority=14)
    public void Test_Update_Post_NotValidAuth() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .put(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());

    }

    @Test (groups = "create_post",priority=15)
    public void Test_Delete_Post_NotValidAuth() {

        Post postTest = new Post(DataHelper.generateRandomTitle(), DataHelper.generateRandomContent());

        given()
                .spec(RequestSpecs.generateFakeToken())
                .body(postTest)
                .delete(resourcePath + "/" + createdPost.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());

    }
}
