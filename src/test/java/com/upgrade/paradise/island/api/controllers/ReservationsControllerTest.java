package com.upgrade.paradise.island.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.paradise.island.api.controllers.validators.ReservationValidator;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.services.DateTimeProvider;
import com.upgrade.paradise.island.api.services.ReservationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationsControllerTest {


    @Mock
    private ReservationService reservationService;


    private ReservationValidator reservationValidator;

    @Mock
    private DateTimeProvider dateTimeProvider;


    private MockMvc mockMvc;


    private static final Integer ID = 11;
    private static final String FULLNAME = "fullname";
    private static final String EMAIL = "email";
    private static final LocalDate START_DATE = LocalDate.of(2020, 3, 5);
    private static final LocalDate END_DATE = LocalDate.of(2020, 3, 6);


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        reservationValidator = new ReservationValidator(dateTimeProvider);

        ReservationsController reservationsController = spy(
            new ReservationsController(reservationService, reservationValidator)
        );

        this.mockMvc = MockMvcBuilders.standaloneSetup(reservationsController).build();

        when(dateTimeProvider.today()).thenReturn(LocalDate.of(2020, 3, 3));
        when(dateTimeProvider.nextMonth()).thenReturn(LocalDate.of(2020, 4, 3));
        when(dateTimeProvider.now()).thenReturn(LocalDate.of(2020, 3, 3).atTime(11, 0));
    }


    @Test
    public void getReservation() throws Exception {

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(START_DATE)
            .endDate(END_DATE);

        String newReservation = "{\n" +
            "  \"fullname\": \"" + FULLNAME + "\"," +
            "  \"email\": \"" + EMAIL + "\"," +
            "  \"startDate\": \"" + START_DATE.toString() + "\"," +
            "  \"endDate\": \"" + END_DATE.toString() + "\"" +
            "}";

        ReservationDto result = (ReservationDto) new ReservationDto()
            .id(ID)
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(START_DATE)
            .endDate(END_DATE);

        when(reservationService.createReservation(eq(newReservationDto)))
            .thenReturn(result);

        mockMvc
            .perform(
                post("/reservations")
                    .content(newReservation)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
//            .andExpect(().isOk())
            .andExpect(jsonPath("$.fullname").value(FULLNAME))
            .andExpect(jsonPath("$.startDate").value(START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(END_DATE.toString()))
        ;
    }

    private String serialize(Object obj, boolean pretty) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();


        if (pretty) {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }

        return mapper.writeValueAsString(obj);
    }

}