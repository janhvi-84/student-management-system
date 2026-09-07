package com.janhvi.studentmanagementapi.controller;

import com.janhvi.studentmanagementapi.audit.AuditLogService;
import com.janhvi.studentmanagementapi.entity.Student;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.StudentRepository;
import com.janhvi.studentmanagementapi.repository.UserRepository;
import com.janhvi.studentmanagementapi.security.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    // ========================================
    // REPOSITORIES / SERVICES
    // ========================================

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;


    // ========================================
    // CONSTRUCTOR
    // ========================================

    public AuthController(
            UserRepository userRepository,
            StudentRepository studentRepository,
            JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {

        this.userRepository = userRepository;

        this.studentRepository = studentRepository;

        this.jwtService = jwtService;

        this.passwordEncoder = passwordEncoder;

        this.auditLogService = auditLogService;
    }


    // ========================================
    // STUDENT REGISTER
    // ========================================

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            // ========================================
            // VALIDATE EMAIL
            // ========================================

            if (request.getEmail() == null ||
                    request.getEmail().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email is required");
            }


            String email =
                    request.getEmail().trim();


            // ========================================
            // CHECK EMAIL
            // ========================================

            if (userRepository
                    .findByEmailAndDeletedFalse(email)
                    .isPresent()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email already exists");
            }


            // ========================================
            // VALIDATE PASSWORD
            // ========================================

            if (request.getPassword() == null ||
                    request.getPassword().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }


            if (request.getPassword().length() < 6) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Password must be at least 6 characters"
                        );
            }


            // ========================================
            // CREATE USER
            // ========================================

            User user = new User();

            user.setEmail(email);

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );

            user.setRole("STUDENT");


            // ========================================
            // SAVE USER
            // ========================================

            User savedUser =
                    userRepository.saveAndFlush(user);


            // ========================================
            // CHECK USER ID
            // ========================================

            if (savedUser.getId() == null) {

                throw new RuntimeException(
                        "User ID was not generated"
                );
            }


            System.out.println(
                    "REGISTER USER ID = "
                            + savedUser.getId()
            );


            // ========================================
            // CREATE STUDENT
            // ========================================

            Student student = new Student();

            student.setName(
                    request.getName()
            );

            student.setAge(
                    request.getAge()
            );

            student.setCourse(
                    request.getCourse()
            );

            student.setDepartment(
                    request.getDepartment()
            );

            student.setCity(
                    request.getCity()
            );

            student.setPhoneNumber(
                    request.getPhoneNumber()
            );

            student.setAddress(
                    request.getAddress()
            );

            student.setStatus(
                    "ACTIVE"
            );

            student.setCreatedAt(
                    LocalDateTime.now()
            );


            // ========================================
            // LINK USER
            // ========================================

            student.setUser(savedUser);


            // ========================================
            // IMPORTANT
            // STUDENT SELF REGISTRATION
            // ========================================

            student.setCreatedBy(
                    savedUser.getId()
            );


            // ========================================
            // SAVE STUDENT
            // ========================================

            Student savedStudent =
                    studentRepository.saveAndFlush(student);


            // ========================================
            // CHECK STUDENT ID
            // ========================================

            if (savedStudent.getId() <= 0) {

                throw new RuntimeException(
                        "Student ID was not generated"
                );
            }


            System.out.println(
                    "REGISTER STUDENT ID = "
                            + savedStudent.getId()
            );


            // ========================================
            // AUDIT LOG
            // STUDENT REGISTRATION
            // ========================================

            auditLogService.log(

                    savedUser.getId(),

                    savedUser.getEmail(),

                    savedUser.getRole(),

                    "REGISTRATION",

                    "AUTH",

                    "Student registered successfully"
            );


            // ========================================
            // SUCCESS RESPONSE
            // ========================================

            return ResponseEntity.ok(

                    Map.of(

                            "message",
                            "Student registered successfully",

                            "userId",
                            savedUser.getId(),

                            "studentId",
                            savedStudent.getId(),

                            "email",
                            savedUser.getEmail(),

                            "role",
                            savedUser.getRole()
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Registration failed: "
                                    + e.getMessage()
                    );
        }
    }


    // ========================================
    // LOGIN
    // ========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            // ========================================
            // VALIDATE EMAIL
            // ========================================

            if (request.getEmail() == null ||
                    request.getEmail().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email is required");
            }


            // ========================================
            // VALIDATE PASSWORD
            // ========================================

            if (request.getPassword() == null ||
                    request.getPassword().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }


            // ========================================
            // FIND USER
            // ========================================

            User user =
                    userRepository
                            .findByEmailAndDeletedFalse(
                                    request.getEmail().trim()
                            )
                            .orElse(null);


            // ========================================
            // USER NOT FOUND
            // ========================================

            if (user == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Invalid email or password"
                        );
            }


            // ========================================
            // CHECK PASSWORD
            // ========================================

            boolean passwordMatches =
                    passwordEncoder.matches(

                            request.getPassword(),

                            user.getPassword()
                    );


            if (!passwordMatches) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Invalid email or password"
                        );
            }


            // ========================================
            // GENERATE JWT
            // ========================================

            String token =
                    jwtService.generateToken(

                            user.getId(),

                            user.getEmail(),

                            user.getRole()
                    );


            // ========================================
            // AUDIT LOG - LOGIN
            // ========================================

            auditLogService.log(

                    user.getId(),

                    user.getEmail(),

                    user.getRole(),

                    "LOGIN",

                    "AUTH",

                    "User login successful"
            );


            // ========================================
            // LOGIN RESPONSE
            // ========================================

            return ResponseEntity.ok(

                    Map.of(

                            "message",
                            "Login successful",

                            "token",
                            token,

                            "id",
                            user.getId(),

                            "email",
                            user.getEmail(),

                            "role",
                            user.getRole()
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Login failed: "
                                    + e.getMessage()
                    );
        }
    }


    // ========================================
    // REGISTER REQUEST
    // ========================================

    public static class RegisterRequest {

        private String name;

        private Integer age;

        private String course;

        private String email;

        private String department;

        private String city;

        private String phoneNumber;

        private String address;

        private String password;


        public RegisterRequest() {
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public Integer getAge() {
            return age;
        }


        public void setAge(Integer age) {
            this.age = age;
        }


        public String getCourse() {
            return course;
        }


        public void setCourse(String course) {
            this.course = course;
        }


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
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


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }
    }


    // ========================================
    // LOGIN REQUEST
    // ========================================

    public static class LoginRequest {

        private String email;

        private String password;


        public LoginRequest() {
        }


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }
    }
}