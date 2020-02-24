package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.ParadiseIslandApplication;
import com.upgrade.paradise.island.api.controllers.validators.ReservationValidator;
import com.upgrade.paradise.island.api.db.dao.ReservationRepository;
import com.upgrade.paradise.island.api.db.model.Reservation;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.exceptions.CampsiteNotAvailableException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParadiseIslandApplication.class)
public class ReservationServiceImplIntegrationTest {

//    @Autowired
    private ReservationServiceImpl reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        reservationService = new ReservationServiceImpl(reservationRepository);
    }

    @Test
    @Transactional
    public void createReservation() {

        LocalDate startDate = LocalDate.of(2020, 3, 9);
        LocalDate endDate = startDate.plusDays(2);
        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname("bob")
            .email("bob@test.com")
            .startDate(startDate)
            .endDate(endDate);

        ReservationDto reservationDto = reservationService.createReservation(newReservationDto);
        Reservation persistedReservation = reservationRepository.getOne(reservationDto.getId());
        assertEquals(persistedReservation.getFullname(), "bob");
        assertEquals(persistedReservation.getEmail(), "bob@test.com");
        assertEquals(persistedReservation.getStartDate(), startDate);
        assertEquals(persistedReservation.getEndDate(), endDate);
    }


    @Test
    @Transactional
    public void createOverlappingReservations() {

        LocalDate startDate = LocalDate.of(2020, 3, 14);
        LocalDate endDate = startDate.plusDays(2);
        NewReservationDto newReservationDto1 = new NewReservationDto()
            .fullname("bob")
            .email("bob@test.com")
            .startDate(startDate)
            .endDate(endDate);

        NewReservationDto newReservationDto2 = new NewReservationDto()
            .fullname("dan")
            .email("dan@test.com")
            .startDate(startDate)
            .endDate(endDate);

        ReservationDto reservationDto1 = reservationService.createReservation(newReservationDto1);

        try {
           ReservationDto reservationDto2 = reservationService.createReservation(newReservationDto2);
           fail("Should have thrown a CampsiteNotAvailableException but didn't.");
        } catch(CampsiteNotAvailableException ex) {

            Reservation persistedReservation = reservationRepository.getOne(reservationDto1.getId());
            assertEquals(persistedReservation.getFullname(), "bob");
            assertEquals(persistedReservation.getEmail(), "bob@test.com");
            assertEquals(persistedReservation.getStartDate(), startDate);
            assertEquals(persistedReservation.getEndDate(), endDate);
        }
    }


    // todo ; to find a fix for that test !
//    @Test
//    @Transactional
//    public void updateReservationFailRollbackToOldReservation() {
//
//        LocalDate startDate1 = LocalDate.of(2020, 3, 14);
//        LocalDate endDate1 = startDate1.plusDays(2);
//        NewReservationDto newReservationDto1 = new NewReservationDto()
//            .fullname("bob")
//            .email("bob@test.com")
//            .startDate(startDate1)
//            .endDate(endDate1);
//
//        LocalDate startDate2 = LocalDate.of(2020, 3, 23);
//        LocalDate endDate2 = startDate2.plusDays(2);
//        NewReservationDto newReservationDto2 = new NewReservationDto()
//            .fullname("dan")
//            .email("dan@test.com")
//            .startDate(startDate2)
//            .endDate(endDate2);
//
//        ReservationDto reservationDto1 = reservationService.createReservation(newReservationDto1);
//        ReservationDto reservationDto2 = reservationService.createReservation(newReservationDto2);
//
//        try {
//            LocalDate newStartDate2 = LocalDate.of(2020, 3, 14);
//            LocalDate newEndDate2 = newStartDate2.plusDays(2);
//            NewReservationDto newNewReservationDto2 = new NewReservationDto()
//                .fullname("dan")
//                .email("dan@test.com")
//                .startDate(newStartDate2)
//                .endDate(newEndDate2);
//            reservationService.updateReservation(reservationDto2.getId(), newNewReservationDto2);
//            fail("Should have thrown a CampsiteNotAvailableException but didn't.");
//        } catch(CampsiteNotAvailableException ex) {
//            try {
//                reservationRepository.flush();
//            } catch (DataIntegrityViolationException dataEx) {
//
//            }
//            Reservation persistedReservation1 = reservationRepository.getOne(reservationDto1.getId());
//            Reservation persistedReservation2 = reservationRepository.getOne(reservationDto2.getId());
//            Optional<Reservation> optPersistedReservation2 = reservationRepository.findById(reservationDto2.getId());
//            assertEquals("dan", persistedReservation2.getFullname());
//            assertEquals("dan@test.com", persistedReservation2.getEmail());
//            assertEquals(startDate2, persistedReservation2.getStartDate());
//            assertEquals(endDate2, persistedReservation2.getEndDate());
//        }
//    }

}
