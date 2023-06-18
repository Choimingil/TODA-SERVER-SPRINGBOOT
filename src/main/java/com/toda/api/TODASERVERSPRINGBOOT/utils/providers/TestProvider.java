package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestProvider {
    private final MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private String uri;
    private String header;
    private String pathVar;
    private String queryString;
    private String body;
    private MvcResult result;

    // Singleton Pattern
    private TestProvider(
            MockMvc mvc,
            String header
    ){
        this.mvc = mvc;
        this.header = header;
    }
    private static TestProvider testProvider = null;
    public static TestProvider getInstance(
            MockMvc mvc,
            String header
    ){
        if(testProvider == null){
            testProvider = new TestProvider(mvc, header);
        }
        return testProvider;
    }

    public <T> T doGetTest(Class<T> c) throws Exception{
        if(header == null){
            result = mvc.perform(get(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
        }
        else{
            result = mvc.perform(get(uri)
                            .header(TokenProvider.HEADER_NAME,header)
                            // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
        }

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return mapper.readValue(content, c);
    }

    public <T> T doPostTest(Class<T> c) throws Exception{
        result = mvc.perform(post(uri)
                        .header(TokenProvider.HEADER_NAME,header)
                        .content(body)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return mapper.readValue(content, c);
    }

    public void setUri(String uri){
        this.uri = uri;
    }

    public void setHeader(String header){
        this.header = header;
    }

    public void setPathVar(String pathVar){
        this.pathVar = pathVar;
    }

    public void setQueryString(String queryString){
        this.queryString = queryString;
    }

    public void setBody(String body){
        this.body = body;
    }
}
