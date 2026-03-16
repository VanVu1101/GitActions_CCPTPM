package com.example.LeVanVu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class OrderApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        String requestBody = """
                {
                  \"customerName\": \"Nguyen Van A\",
                  \"phone\": \"0900000000\",
                  \"address\": \"123 Duong ABC, Ha Noi\",
                  \"items\": [
                    { \"productId\": 1, \"quantity\": 2 },
                    { \"productId\": 2, \"quantity\": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalAmount").exists());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(38));
    }

    @Test
    void shouldRejectInvalidOrderRequest() throws Exception {
        String requestBody = """
                {
                  \"customerName\": \"\",
                  \"phone\": \"\",
                  \"address\": \"\",
                  \"items\": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dữ liệu gửi lên không hợp lệ"))
                .andExpect(jsonPath("$.fieldErrors.customerName").exists())
                .andExpect(jsonPath("$.fieldErrors.phone").exists())
                .andExpect(jsonPath("$.fieldErrors.address").exists())
                .andExpect(jsonPath("$.fieldErrors.items").exists());
    }
}
