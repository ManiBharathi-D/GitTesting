package RestAssured.RESTAPI;

import io.restassured.*;
import io.restassured.builder.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.*;

import static io.restassured.RestAssured.*;

import java.io.File;

public class SpecBuilder {
    public static void main(String[] args) {
        //RestAssured.baseURI ="https://rahulshettyacademy.com/api/ecom";
        
         //Login to the website
        String UserId, Auth;
        testData td = new testData();
        td.setUserEmail("dmanibharathi2000@gmail.com");
        td.setUserPassword("Dummy@123");
        RequestSpecification rs= new RequestSpecBuilder().setContentType(ContentType.JSON).setBaseUri("https://rahulshettyacademy.com/api/ecom").build();
        ResponseSpecification rp = new ResponseSpecBuilder().expectStatusCode(200).build();
        String res =given().log().all().spec(rs).body(td)
        		.when().log().all().post("/auth/login")
        .then().log().all().spec(rp).extract().response().asString();
        
        JsonPath js= new JsonPath(res);
        UserId = js.getString("userId");
        Auth = js.getString("token");
        
        System.out.println(UserId +"\n" + Auth);   
        
        //Create a product in the website
        String ProductId;
        RequestSpecification rs1= new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com/api/ecom").addHeader("Authorization", Auth).build();
        ResponseSpecification rp1 = new ResponseSpecBuilder().expectStatusCode(200).build();
        
        String res1 =given().log().all().spec(rs1).param("productName", "Kanguva").param("productAddedBy", UserId)
        .param("productCategory", "Movies").param("productSubCategory", "Kollywood")
        .param("productPrice", 1150).param("productDescription", "Surya's Movie Ticket")
        .param("productFor", "Fans").multiPart("productImage", new File(".\\Image Kanguva.jpg"))
        .when().post("/product/add-product").then().log().all().assertThat().statusCode(201).extract().response().asString();
        
        JsonPath js1 = new JsonPath(res1);
        ProductId= js1.getString("productId");
        System.out.println(ProductId);
        
        //Get Product details
        TestData3 res2 = given().spec(rs1).when().get("/product/get-product-detail/"+ProductId).then().spec(rp1).extract().response().as(TestData3.class);
        System.out.println(res2.getMessage());
        System.out.println(res2.getData().getProductAddedBy());
        
        //System.out.println(res2);
        //JsonPath js2 = new JsonPath(res2);
       // JsonPath js3 = js2.getJsonObject("data");
        //System.out.println(js3);
        //String ImageDetails = js2.getString("data.productImage");
        //System.out.println(ImageDetails);
        //JsonPath js3 = new JsonPath(res2("data"));
        //String TestData = js2.getString("data");
        
       //Add to Cart
        String res3 = given().log().all().spec(rs1)
        		.header("Content-Type","application/json")
        		.body("{\r\n"
        				+ "    \"_id\": \""+res2.getData().get_id()+"\",\r\n"
        				+ "    \"product\": "+ res2.getData()+"\r\n"
        				+ "}").when().post("/user/add-to-cart")
        .then().log().all().spec(rp1).extract().response().asString();
        
        	System.out.println(res3);
        	
        //Delete the product
        String res4 = given().log().all().spec(rs1).header("Content-Type","application/json")
        		.when().delete("product/delete-product/"+ProductId).then().log().all().spec(rp1).extract().response().asString();
        System.out.println(res4);
    }
}
