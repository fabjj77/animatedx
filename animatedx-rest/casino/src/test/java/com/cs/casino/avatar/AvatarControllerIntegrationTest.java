package com.cs.casino.avatar;

import com.cs.avatar.AvatarService;
import com.cs.casino.player.TestConfig;
import com.cs.persistence.NotFoundException;
import com.cs.persistence.Status;
import com.cs.rest.ExceptionMapper;
import com.cs.rest.status.NotFoundMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Omid Aleapour
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, AvatarController.class, ExceptionMapper.class})
public class AvatarControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    @Qualifier("avatarService")
    private AvatarService avatarService;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void getAvatarBaseTypes_ShouldReturn404WhenAvatarBaseTypeDoesNotExist()
            throws Exception {
        final Status status = Status.INACTIVE;

        final NotFoundException notFoundException = new NotFoundException(status);
        final String expectedContent = mapper.writeValueAsString(NotFoundMessage.of(notFoundException));

        when(avatarService.getAvatarBaseTypes(status)).thenThrow(notFoundException);

        mockMvc.perform(
                get("/api/avatars/status/{status}", status).contentType(APPLICATION_JSON).accept(APPLICATION_JSON).header("X-Forwarded-For", "127.0.0.1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedContent));
    }
}
