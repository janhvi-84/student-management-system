package com.janhvi.studentmanagementapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.janhvi.studentmanagementapi.entity.Student;

@Repository
public interface StudentRepository
        extends JpaRepository<Student, Integer> {

    // ========================================
    // GET ALL ACTIVE (NOT DELETED)
    // ========================================

    List<Student> findByDeletedFalse();


    // ========================================
    // SEARCH BY NAME
    // ========================================

    List<Student> findByNameContainingIgnoreCaseAndDeletedFalse(
            String name
    );


    // ========================================
    // SEARCH BY DEPARTMENT
    // ========================================

    List<Student> findByDepartmentContainingIgnoreCaseAndDeletedFalse(
            String department
    );


    // ========================================
    // SEARCH BY CITY
    // ========================================

    List<Student> findByCityContainingIgnoreCaseAndDeletedFalse(
            String city
    );


    // ========================================
    // FIND STUDENT BY USER ID
    // ========================================

    Student findByUserIdAndDeletedFalse(
            Long userId
    );


    // ========================================
    // FILTER BY STATUS
    // ========================================

    List<Student> findByStatusAndDeletedFalse(
            String status
    );


    // ========================================
    // PAGINATION
    // ========================================

    Page<Student> findByDeletedFalse(
            Pageable pageable
    );


    // ========================================
    // SORT BY NAME ASC
    // ========================================

    List<Student> findByDeletedFalseOrderByNameAsc();


    // ========================================
    // SORT BY NAME DESC
    // ========================================

    List<Student> findByDeletedFalseOrderByNameDesc();


    // ========================================
    // SORT BY CREATED DATE
    // ========================================

    List<Student> findByDeletedFalseOrderByCreatedAtDesc();


    // ========================================
    // RECENT STUDENT
    // ========================================

    Student findTopByDeletedFalseOrderByCreatedAtDesc();


    // ========================================
    // RECYCLE BIN
    // ========================================

    List<Student> findByDeletedTrue();


    // ========================================
    // RECYCLE BIN - RECENT FIRST
    // ========================================

    List<Student> findByDeletedTrueOrderByCreatedAtDesc();
}