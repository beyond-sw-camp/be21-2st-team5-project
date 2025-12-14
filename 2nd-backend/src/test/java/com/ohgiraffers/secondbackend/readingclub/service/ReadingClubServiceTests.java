package com.ohgiraffers.secondbackend.readingclub.service;

import com.ohgiraffers.secondbackend.readingclub.dto.request.ReadingClubRequestDTO;
import com.ohgiraffers.secondbackend.readingclub.dto.response.ReadingClubResponseDTO;
import com.ohgiraffers.secondbackend.readingclub.repository.ReadingClubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReadingClubServiceTests {

    @Autowired
    private ReadingClubService readingClubService;

    @Autowired
    private ReadingClubRepository readingClubRepository;

    @Test
    void createReadingClub(){
        // given
        ReadingClubRequestDTO req = new ReadingClubRequestDTO();
        req.setName("test");
        req.setDescription("test");
        req.setCategoryId(1);
        int hostId = 1;

        // when
        ReadingClubResponseDTO res = readingClubService.createReadingClub(req, hostId);
        // then
        assertNotNull(res);
        assertEquals("test", res.getName());
        assertEquals(hostId, res.getHostUserId());
        assertEquals(1, res.getCategoryId());
    }


}