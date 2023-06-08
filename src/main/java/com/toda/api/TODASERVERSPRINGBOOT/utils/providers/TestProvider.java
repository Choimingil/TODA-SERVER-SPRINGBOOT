package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

import com.toda.api.TODASERVERSPRINGBOOT.utils.filters.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TestProvider {
    public final String secret;
    public final long tokenValidityInMilliseconds;

    public TestProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds
    ){
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    public MvcResult doGetTestWithNoHeader(MockMvc mvc, String uri) throws Exception {
        return mvc.perform(get(uri)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
//                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }


    public MvcResult doGetTestWithHeader(MockMvc mvc, String uri, String header) throws Exception {
        return mvc.perform(get(uri)
                        .header(JwtFilter.HEADER_NAME,header)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }


    public MvcResult doPostTestWithOnlyHeader(MockMvc mvc, String uri, String header) throws Exception {
        return mvc.perform(post(uri)
                        .header(JwtFilter.HEADER_NAME,header)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    public MvcResult doPostTestWithOnlyBody(MockMvc mvc, String uri, String body)  throws Exception{
        return mvc.perform(post(uri)
                        .content(body)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    public MvcResult doPostTestWithHeaderAndBody(MockMvc mvc, String uri, String header, String body) throws Exception {
        return mvc.perform(post(uri)
                        .header(JwtFilter.HEADER_NAME,header)
                        .content(body)
                        // 받을 데이터 타입 설정 --> JSON으로 받기 때문에 해당 설정 ON
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }
}
