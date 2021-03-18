import helpers.DataHelper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Comment;
import model.Post;
import org.hamcrest.core.IsEqual;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import specifications.RequestSpecs;
import specifications.ResponseSpecs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class CommentsTest extends BaseTest {
    private static String resourcePath = "/v1/post";
    private static String resourcePathComment = "/v1/comment";
    private static String resourcePathGetComments = "/v1/comments";
    private static Integer createdPost = 0;
    private static Integer createdComment= 0;

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


    @BeforeGroups("create_comment")

    public void Test_Create_Comment_default() {
       Test_Create_Post();
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());
        Response response = given()
       .auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .post(resourcePathComment + "/" + createdPost);

        JsonPath jsonPathEvaluator = response.jsonPath();
        createdComment = jsonPathEvaluator.get("id");
    }

    @Test(groups = "create_post", priority=1)
    public void Test_Create_Comment() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .post(resourcePathComment + "/" + createdPost)
                .then()
                .body(containsString("id"))
                .body(containsString("message"))
                .body("message", IsEqual.equalTo("Comment created"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test
    (groups = "create_comment", priority=2)
    public void Test_Get_All_Comments() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .get(resourcePathGetComments + "/"+ createdPost.toString())
                .then()
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("comment"))
                .body(containsString("updated_at"))
                .body(containsString("created_at"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test
    (groups = "create_comment", priority=3)
    public void Test_Get_One_Comment() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .get(resourcePathComment + "/"+ createdPost  +"/"+ createdComment.toString())
                .then()
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("comment"))
                .body(containsString("updated_at"))
                .body(containsString("created_at"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=4)
    public void Test_Update_One_Comment() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .put(resourcePathComment + "/" + createdPost +"/"+ createdComment.toString())
                .then()
                .body("message", IsEqual.equalTo("Comment updated"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=5)
    public void Test_Delete_One_Comment() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .delete(resourcePathComment + "/" + createdPost +"/"+ createdComment.toString())
                .then()
                .body("message", IsEqual.equalTo("Comment deleted"))
                .statusCode(200)
                .spec(ResponseSpecs.defaultSpec());
    }



    ///////******************************** Negative Testing ******************************///////////////////////////


    @Test(groups = "create_post", priority=6)
    public void Test_Create_Comment_MandatoryField_Fail() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), "");

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .post(resourcePathComment + "/" + createdPost)
                .then()
                .statusCode(406)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test(priority=7)
    public void Test_Get_All_Comments_Error() {
        Comment commentTest = new Comment("", "");

        given().auth()
                .basic("testuser", "testpasss")
                .body("")
                .get(resourcePathGetComments + "/0")
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=8)
    public void Test_Get_One_Comment_Doesnt_Exist() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .get(resourcePathComment + "/7439999"  +"/"+ createdComment.toString())
                .then()
                .body(containsString("error"))
                .body("Message",equalTo("Comment not found"))
                .statusCode(404)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=9)
    public void Test_Update_One_Comment_Doesnt_Exist() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .put(resourcePathComment + "/74377777"  +"/"+ createdComment.toString())
                .then()
                .body(containsString("error"))
                .body("message",equalTo("Comment could not be updated"))
                .statusCode(406)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=10)
    public void Test_Delete_One_Comment_Error() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuth(),DataHelper.passAuth())
                .body(commentTest)
                .delete(resourcePathComment + "/74377777"  +"/"+ createdComment.toString())
                .then()
                .body(containsString("error"))
                .body("message",equalTo("Comment could not be deleted"))
                .statusCode(406)
                .spec(ResponseSpecs.defaultSpec());
    }


    /////////////////////***********Test Security**********************/////////////////////////////////////

    @Test(groups = "create_post", priority=11)
    public void Test_Create_Comment_NotValidAuth() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuthWrong(),DataHelper.passAuthWrong())
                .body(commentTest)
                .post(resourcePathComment + "/" + createdPost)
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }

    @Test(priority=12)
    //(groups = "create_post")
    public void Test_Get_All_Comments_NotValidAuth() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuthWrong(),DataHelper.passAuthWrong())
                .body(commentTest)
                .get(resourcePathGetComments + "/"+ createdPost.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=13)
    public void Test_Get_One_Comment_NotValidAuth() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuthWrong(),DataHelper.passAuthWrong())
                .body(commentTest)
                .get(resourcePathComment + "/"+ createdPost  +"/"+ createdComment.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=14)
    public void Test_Update_One_Comment_NotValidAuth() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuthWrong(),DataHelper.passAuthWrong())
                .body(commentTest)
                .put(resourcePathComment + "/" + createdPost +"/"+ createdComment.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
    @Test(groups = "create_comment", priority=15)
    public void Test_Delete_One_Comment_NotValidAuth() {
        Comment commentTest = new Comment(DataHelper.generateRandomName(), DataHelper.generateRandomComment());

        given().auth()
                .basic(DataHelper.userAuthWrong(),DataHelper.passAuthWrong())
                .body(commentTest)
                .delete(resourcePathComment + "/" + createdPost +"/"+ createdComment.toString())
                .then()
                .statusCode(401)
                .spec(ResponseSpecs.defaultSpec());
    }
}

