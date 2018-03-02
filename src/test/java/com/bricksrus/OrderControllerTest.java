package com.bricksrus;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderReferenceRepository orderReferenceRepository;

    @Before
    public void before() {
        // Clear all data between every test
        orderReferenceRepository.deleteAll();
    }

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

    @Test
    public void getAllOrders_NoOrdersExist_ShouldSucceed() throws Exception {
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONArray obj = new JSONArray(result.getResponse().getContentAsString());
        assertEquals(0, obj.length());
    }

    @Test
    public void getAllOrders_1Order_ShouldSucceed() throws Exception {
        int id = createOrder(500);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONArray obj = new JSONArray(result.getResponse().getContentAsString());

        assertEquals(1, obj.length());
        JSONObject first = (JSONObject) obj.get(0);
        assertEquals(id, first.getInt("id"));
        assertEquals(500, first.getInt("numBricks"));
    }

    @Test
    public void getAllOrders_2Orders_ShouldSucceed() throws Exception {
        int id1 = createOrder(500);
        int id2 = createOrder(1000);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONArray obj = new JSONArray(result.getResponse().getContentAsString());

        assertEquals(2, obj.length());

        JSONObject first = (JSONObject) obj.get(0);
        JSONObject second = (JSONObject) obj.get(1);

        // The orders are guaranteed to be sorted by their ids
        assertEquals(id1, first.getInt("id"));
        assertEquals(500, first.getInt("numBricks"));
        assertEquals(id2, second.getInt("id"), id2);
        assertEquals(1000, second.getInt("numBricks"));
    }

    @Test
    public void updateOrder_OrderExists_ShouldSucceed() throws Exception {
        int id = createOrder(1000);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.put(OrderRequestController.ENDPOINT_ORDER + id)
                        .content("{\"numBricks\":2000}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        int idReturned = obj.getInt("id");

        assertEquals(idReturned, id);

        // Now get all orders to check
        MvcResult resultAll = mvc.perform(
                MockMvcRequestBuilders.get(OrderRequestController.ENDPOINT_ORDER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONArray results = new JSONArray(resultAll.getResponse().getContentAsString());
        assertEquals(1, results.length());
        JSONObject first = (JSONObject) results.get(0);
        assertEquals(2000, first.getInt("numBricks"));
    }

    @Test
    public void updateOrder_BadRequest_ShouldFail() throws Exception {
        int id = createOrder(1000);

        mvc.perform(
                MockMvcRequestBuilders.put(OrderRequestController.ENDPOINT_ORDER + id)
                        .content("{\"numBricks\":0}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    public void updateOrder_OrderDoesNotExist_ShouldFail() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.put(OrderRequestController.ENDPOINT_ORDER + 1000)
                        .content("{\"numBricks\":1000}")
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }
}
