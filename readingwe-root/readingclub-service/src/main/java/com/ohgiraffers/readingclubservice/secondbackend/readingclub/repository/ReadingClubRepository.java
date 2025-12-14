package com.ohgiraffers.readingclubservice.secondbackend.readingclub.repository;

import com.ohgiraffers.readingclubservice.secondbackend.readingclub.entity.ReadingClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingClubRepository extends JpaRepository<ReadingClub, Long> {

    List<ReadingClub> findByUserId(long userId);
}
