//package com.techelevator;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.techelevator.tenmo.model.Transfer;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import java.math.BigDecimal;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//@RunWith(SpringRunner.class)
//@SpringBootConfiguration
//@SpringBootTest
//@ContextConfiguration
//@AutoConfigureMockMvc
//public class UserControllerTests {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @BeforeAll
//    public void setUp() {
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    public void allMethods_ExpectUnauthorized() throws Exception {
//        Transfer transfer = new Transfer(
//                3001,
//                2,
//                2,
//                2001,
//                2002,
//                BigDecimal.valueOf(100.00));
//
//        mvc.perform(get("/transfer/1")).andExpect(status().isUnauthorized());
//        mvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON).content(toJson(transfer))).andExpect(status().isUnauthorized());
//
//    }
//
//    private String toJson(Transfer transfer) throws JsonProcessingException {
//        return mapper.writeValueAsString(transfer);
//    }
//
//}
