package com.bricksrus;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void createOrder_1000Bricks_ShouldReturnOrderRef() throws Exception {
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(OrderRequestController.ENDPOINT_ORDER)
                        .content("{\"numBricks\":1000}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        int id = obj.getInt("id");
    }

    @Test
    public void createOrder_NoArgs_ShouldFail() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post(OrderRequestController.ENDPOINT_ORDER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    public void createOrder_NegativeBricks_ShouldFail() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post(OrderRequestController.ENDPOINT_ORDER)
                        .content("{\"numBricks\":-1}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    private int createOrder(int numBricks) throws Exception {
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(OrderRequestController.ENDPOINT_ORDER)
                        .content("{\"numBricks\":" + numBricks + "}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        int id = obj.getInt("id");
        return id;
    }

    @Test
    public void getOrder_InvalidId_ShouldFail() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER + "10000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    public void getOrder_GoodId_ShouldSucceed() throws Exception {
        int numBricksToCreate = 10000;
        int id = createOrder(numBricksToCreate);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        int returnedId = obj.getInt("id");
        int returnedNumBricks = obj.getInt("numBricks");

        assertEquals(id, returnedId);
        assertEquals(returnedNumBricks, numBricksToCreate);
    }

}
