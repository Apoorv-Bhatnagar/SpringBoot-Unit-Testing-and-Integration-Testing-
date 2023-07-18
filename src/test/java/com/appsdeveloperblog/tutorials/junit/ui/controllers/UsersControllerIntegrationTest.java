package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,properties = "server.port=8089")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//in properties we need to write the config which we need to overrite
@TestPropertySource(locations = "/application-test.properties",properties = "server.port=8082")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIntegrationTest {
    @Value("${server.port}")
    private int serverPort;
    @Autowired
private TestRestTemplate restTemplate;
    private String authorizationToken;
    @Test
    void contextLoads()
    {
        System.out.println("----"+serverPort);
    }

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void RestCreateUser_WhenValidDetailsProvided() throws JSONException {
//Arrange
        JSONObject userDetailsRequestJSON=new JSONObject();
        userDetailsRequestJSON.put("firstName","Apoorv");
        userDetailsRequestJSON.put("lastName","bhatnagar");
        userDetailsRequestJSON.put("email","a@gmail.com");
        userDetailsRequestJSON.put("password","12345");
        userDetailsRequestJSON.put("repeatPassword","12345");

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request=new HttpEntity<>(userDetailsRequestJSON.toString(),headers);
//Act
  ResponseEntity<UserRest> createdUserDetailsEntity= restTemplate.postForEntity("/users",request,UserRest.class);
        UserRest UserDetails=createdUserDetailsEntity.getBody();
//Assert
        Assertions.assertEquals(HttpStatus.OK,createdUserDetailsEntity.getStatusCode());
        Assertions.assertEquals(userDetailsRequestJSON.get("firstName"),UserDetails.getFirstName(),"didnt match");
    }
@Test
    @DisplayName("Get /users requires JWT")
@Order(2)
    void testGetUsers_WhenMissingJwt()
{
    HttpHeaders headers=new HttpHeaders();
    headers.set("Accept","application/json");
    HttpEntity requestEntity=new HttpEntity(null,headers);
    ResponseEntity<List<UserRest>> response = restTemplate.exchange("/users",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<UserRest>>() {
            });
      Assertions.assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode(),"status code 403 should have written");
}
    @Test
    @DisplayName("/login/works")
    @Order(3)
    void testUserLogin_whenValidCredentailsProvided() throws JSONException {
        JSONObject loginCredentials=new JSONObject();
        loginCredentials.put("email","a@gmail.com");
        loginCredentials.put("password","12345");

        HttpEntity<String> request=new HttpEntity<>(loginCredentials.toString());
        ResponseEntity response = restTemplate.postForEntity("/users/login", request, null);

        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode(),"status code should be 200");
        Assertions.assertNotNull(response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0),"should contain headers with jwt");
       authorizationToken=response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);
        Assertions.assertNotNull(response.getHeaders().getValuesAsList("userId").get(0),"should contain headers with userId");

    }

    @Test
    @DisplayName("get /users")
    @Order(3)
    void testGetUsers_WhenValidJwtProvided()
    {
        HttpHeaders headers=new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);
   HttpEntity requestEntity=new HttpEntity(headers);
  ResponseEntity<List<UserRest>> response= restTemplate.exchange("/users",
           HttpMethod.GET,
           requestEntity,
           new ParameterizedTypeReference<List<UserRest>>() {
           });
   Assertions.assertEquals(HttpStatus.OK,response.getStatusCode(),"status code shpould be zero");
Assertions.assertTrue(response.getBody().size()==1,"should have one user");
    }

}
