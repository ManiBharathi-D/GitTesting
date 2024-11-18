package RestAssured.RESTAPI;

import io.restassured.*;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

import java.io.File;

public class App {
    public static void main(String[] args) {
        RestAssured.baseURI ="https://rahulshettyacademy.com/api/ecom";
         //Login to the website
        String UserId, Auth;
        testData td = new testData();
        td.setUserEmail("dmanibharathi2000@gmail.com");
        td.setUserPassword("Dummy@123");
        String res =given().log().all().header("Content-Type", "application/json").body(td)
        		.when().log().all().post("/auth/login")
        .then().log().all().assertThat().statusCode(200).extract().response().asString();
        
        JsonPath js= new JsonPath(res);
        UserId = js.getString("userId");
        Auth = js.getString("token");
        
        System.out.println(UserId +"\n" + Auth);   
        
        //Create a product in the website
        String ProductId;
        String res1 =given().log().all().header("Authorization", Auth).param("productName", "Kanguva").param("productAddedBy", UserId)
        .param("productCategory", "Movies").param("productSubCategory", "Kollywood")
        .param("productPrice", 1150).param("productDescription", "Surya's Movie Ticket")
        .param("productFor", "Fans").multiPart("productImage", new File(".\\Image Kanguva.jpg"))
        .when().post("/product/add-product").then().log().all().assertThat().statusCode(201).extract().response().asString();
        
        JsonPath js1 = new JsonPath(res1);
        ProductId= js1.getString("productId");
        System.out.println(ProductId);
        
        //Get Product details
        String res2 = given().header("Authorization", Auth).when().get("/product/get-product-detail/"+ProductId).then().assertThat().statusCode(200).extract().response().asString();
        System.out.println(res2);
        JsonPath js2 = new JsonPath(res2);
       // JsonPath js3 = js2.getJsonObject("data");
        //System.out.println(js3);
        String ImageDetails = js2.getString("data.productImage");
        System.out.println(ImageDetails);
        //JsonPath js3 = new JsonPath(res2("data"));
        
        //Add to Cart
        String res3 = given().log().all().header("Authorization", Auth).header("Content-Type","application/json").body("{\r\n"
        		+ "    \"_id\": \""+UserId+"\",\r\n"
        		+ "    \"product\": {\r\n"
        		+ "        \"_id\": \""+ProductId+"\",\r\n"
        		+ "        \"productName\": \""+js2.getString("data.productName")+"\",\r\n"
        		+ "        \"productCategory\": \""+js2.getString("data.productCategory")+"\",\r\n"
        		+ "        \"productSubCategory\": \""+js2.getString("data.productSubCategory")+"\",\r\n"
        		+ "        \"productPrice\": "+js2.getInt("data.productPrice")+",\r\n"
        		+ "        \"productDescription\": \""+js2.getString("data.productDescription")+"\",\r\n"
        		+ "        \"productImage\": \""+ImageDetails+"\",\r\n"
        		+ "        \"productRating\": \"0\",\r\n"
        		+ "        \"productTotalOrders\": \"0\",\r\n"
        		+ "        \"productStatus\": true,\r\n"
        		+ "        \"productFor\": \""+js2.getString("data.productFor")+"\",\r\n"
        		+ "        \"productAddedBy\": \""+UserId+"\",\r\n"
        		+ "        \"__v\": 0\r\n"
        		+ "    }\r\n"
        		+ "}").when().post("/user/add-to-cart")
        .then().log().all().assertThat().statusCode(200).extract().response().asString();
        
        	System.out.println(res3);
        	
        //Delete the product
        String res4 = given().log().all().header("Authorization", Auth).header("Content-Type","application/json")
        		.when().delete("product/delete-product/"+ProductId).then().log().all().assertThat().statusCode(200).extract().response().asString();
        System.out.println(res4);
        
        
      
    
    }
}
