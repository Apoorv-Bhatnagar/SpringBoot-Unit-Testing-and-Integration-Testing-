package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

@WebMvcTest(controllers = UsersController.class,excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)
//@MockBean({UsersService.class})
public class UsersControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    //@Autowired
    UsersService usersService;
    @Test
    @DisplayName("user can be created")
    void testCreateUser_whenValidUserDetailsProvided() throws Exception {
        UserDetailsRequestModel userDetailsRequestModel=new UserDetailsRequestModel();
       userDetailsRequestModel.setFirstName("apoorv");
       userDetailsRequestModel.setEmail("apoorv@test.com");
       userDetailsRequestModel.setLastName("bhatnagar");
       userDetailsRequestModel.setPassword("12345");
       userDetailsRequestModel.setRepeatPassword("12345");
      UserDto userDto= new ModelMapper().map(userDetailsRequestModel, UserDto.class);
      userDto.setUserId(UUID.randomUUID().toString());
        Mockito.when(usersService.createUser(Mockito.any(UserDto.class))).thenReturn(userDto);
       RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
      MvcResult mvcResult= mockMvc.perform(requestBuilder).andReturn();
        String responseAsString = mvcResult.getResponse().getContentAsString();
       UserRest userRest = new ObjectMapper().readValue(responseAsString, UserRest.class);
       Assertions.assertEquals(userDetailsRequestModel.getEmail(),userRest.getEmail());
        Assertions.assertEquals(200,mvcResult.getResponse().getStatus(),"false");
    }
}

