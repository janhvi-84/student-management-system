package com.janhvi.studentmanagementapi.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "students")

public class Student extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int age;

    private String course;

    private String department;

    private String city;

    private String phoneNumber;

    private String address;
    @Column(name = "profile_photo")
    private String profilePhoto;

    private String status;
    @Column(name = "deleted")
private boolean deleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // ========================================
    // USER RELATION
    // ========================================

@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", unique = true)
private User user;

    // ========================================
    // DEFAULT CONSTRUCTOR
    // ========================================

    public Student() {
    }

    // ========================================
    // GETTERS AND SETTERS
    // ========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePhoto() {
    return profilePhoto;
}

public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

public boolean isDeleted() {
    return deleted;
}

public void setDeleted(boolean deleted) {
    this.deleted = deleted;
}


}