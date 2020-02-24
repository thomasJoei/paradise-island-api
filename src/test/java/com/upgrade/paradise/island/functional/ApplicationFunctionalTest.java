package com.upgrade.paradise.island.functional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.services.DateTimeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
// to cleanup test database before each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ApplicationFunctionalTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;


    @Autowired
    private MockMvc mockMvc;

    private static final String FULLNAME = "fullname";
    private static final String EMAIL = "email";
    private static final LocalDate START_DATE = LocalDate.of(2020, 3, 5);
    private static final LocalDate END_DATE = LocalDate.of(2020, 3, 6);

    private static final LocalDate TODAY = LocalDate.of(2020, 3, 3);


    @Before
    public void before() {

        LocalDateTime now = TODAY.atTime(12, 1);
        LocalDate tomorrow = TODAY.plusDays(1);
        LocalDate nextMonth = TODAY.plusMonths(1);

        when(dateTimeProvider.today()).thenReturn(TODAY);
        when(dateTimeProvider.now()).thenReturn(now);
        when(dateTimeProvider.tomorrow()).thenReturn(tomorrow);
        when(dateTimeProvider.nextMonth()).thenReturn(nextMonth);
    }


    @Test
    public void createReservation() throws Exception {
        String newReservation = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        mockMvc
            .perform(
                post("/reservations")
                    .content(newReservation)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullname").value(FULLNAME))
            .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
        ;
    }

    @Test
    public void createAndGetReservation() throws Exception {
        String newReservation = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        ReservationDto response = mvcResultToReservationDto(
            mockMvc
                .perform(
                    post("/reservations")
                        .content(newReservation)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(FULLNAME))
                .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
                .andReturn()
        );

        mockMvc
            .perform(
                get("/reservations/{reservationId}", response.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullname").value(FULLNAME))
            .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
        ;
    }


    @Test
    public void overlappingReservationsError() throws Exception {
        String newReservation = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        ReservationDto response = mvcResultToReservationDto(
            mockMvc
                .perform(
                    post("/reservations")
                        .content(newReservation)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(FULLNAME))
                .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
                .andReturn()
        );

        mockMvc
            .perform(
                post("/reservations")
                    .content(newReservation)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity())
        ;

        mockMvc
            .perform(
                get("/reservations/{reservationId}", response.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullname").value(FULLNAME))
            .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
            .andReturn()
        ;
    }

    @Test
    public void updateResultInOverlappingReservationsError() throws Exception {
        String newReservation1 = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        LocalDate startDate2 = START_DATE.plusDays(5);
        LocalDate endDate2 = END_DATE.plusDays(5);
        String newReservation2 = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + startDate2.toString() + "\"," +
            "  \"endDate\": \"" + endDate2.toString() + "\"" +
            "}";

        ReservationDto response1 = mvcResultToReservationDto(
            mockMvc
                .perform(
                    post("/reservations")
                        .content(newReservation1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(FULLNAME))
                .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
                .andReturn()
        );

        ReservationDto response2 = mvcResultToReservationDto(
            mockMvc
                .perform(
                    post("/reservations")
                        .content(newReservation2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(FULLNAME))
                .andExpect(jsonPath("$.startDate").value(startDate2.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate2.toString()))
                .andReturn()
        );


        mockMvc
            .perform(
                put("/reservations/{reservationId}", response2.getId())
                    .content(newReservation1)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnprocessableEntity())
        ;

        mockMvc
            .perform(
                get("/reservations/{reservationId}", response1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullname").value(FULLNAME))
            .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
            .andReturn()
        ;

    }

    @Test
    public void deleteReservation() throws Exception {
        String newReservation = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        ReservationDto response = mvcResultToReservationDto(
            mockMvc
                .perform(
                    post("/reservations")
                        .content(newReservation)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(FULLNAME))
                .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
                .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
                .andReturn()
        );

        mockMvc
            .perform(delete("/reservations/{reservationId}", response.getId()))
            .andExpect(status().isNoContent())
        ;

        mockMvc
            .perform(
                get("/reservations/{reservationId}", response.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
        ;
    }

    private ReservationDto mvcResultToReservationDto(MvcResult mvcResult) throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        return (ReservationDto) ApplicationFunctionalTest.convertJSONStringToObject(json, ReservationDto.class);
    }

    // helper method, would move to a Util class in a real project
    public static <T> Object convertJSONStringToObject(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);
        return mapper.readValue(json, objectClass);
    }

}