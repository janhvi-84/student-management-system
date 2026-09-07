package com.janhvi.studentmanagementapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.janhvi.studentmanagementapi.audit.AuditLogService;

import java.time.LocalDateTime;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.janhvi.studentmanagementapi.entity.Student;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.StudentRepository;
import com.janhvi.studentmanagementapi.repository.UserRepository;

@Service
public class StudentService {

    // =========================================================
    // REPOSITORIES
    // =========================================================

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    // =========================================================
    // GET LOGGED-IN USER ID
    // =========================================================

    private Long getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return null;
        }

        Object principal =
                authentication.getPrincipal();

        // -----------------------------------------------------
        // JWT PRINCIPAL = LONG
        // -----------------------------------------------------

        if (principal instanceof Long) {

            return (Long) principal;
        }

        // -----------------------------------------------------
        // SAFETY - INTEGER
        // -----------------------------------------------------

        if (principal instanceof Integer) {

            return ((Integer) principal).longValue();
        }

        // -----------------------------------------------------
        // SAFETY - STRING USER ID
        // -----------------------------------------------------

        if (principal instanceof String) {

            try {

                return Long.parseLong(
                        (String) principal
                );

            } catch (NumberFormatException e) {

                // String may be email
            }
        }

        // -----------------------------------------------------
        // FALLBACK - EMAIL
        // -----------------------------------------------------

        String email =
                authentication.getName();

        if (email == null ||
                email.isBlank()) {

            return null;
        }

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return null;
        }

        return user.getId();
    }


    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser() {

        Long userId =
                getLoggedInUserId();

        if (userId == null) {

            return null;
        }

        return userRepository
                .findById(userId)
                .orElse(null);
    }


    // =========================================================
    // ADD STUDENT
    // =========================================================

    @Transactional
    public Student addStudent(
            Student student,
            String email,
            String password) {

        // -----------------------------------------------------
        // VALIDATE EMAIL
        // -----------------------------------------------------

        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        email = email.trim();


        // -----------------------------------------------------
        // VALIDATE PASSWORD
        // -----------------------------------------------------

        if (password == null ||
                password.isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }


        // -----------------------------------------------------
        // CHECK EMAIL
        // -----------------------------------------------------

        if (userRepository
                .findByEmail(email)
                .isPresent()) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        // -----------------------------------------------------
        // GET ACTING USER
        // -----------------------------------------------------

        User loggedInUser =
                getLoggedInUser();

        Long loggedInUserId = null;

        if (loggedInUser != null) {

            loggedInUserId =
                    loggedInUser.getId();
        }


        // -----------------------------------------------------
        // CREATE STUDENT USER
        // -----------------------------------------------------

        User user = new User();

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRole("STUDENT");


        // -----------------------------------------------------
        // SAVE USER
        // -----------------------------------------------------

        User savedUser =
                userRepository.saveAndFlush(user);


        // -----------------------------------------------------
        // LINK USER WITH STUDENT
        // -----------------------------------------------------

        student.setUser(savedUser);


        // -----------------------------------------------------
        // CREATED DATE
        // -----------------------------------------------------

        student.setCreatedAt(
                LocalDateTime.now()
        );


        // -----------------------------------------------------
        // CREATED BY
        // -----------------------------------------------------

        /*
         * ADMIN creates student:
         *
         * createdBy = ADMIN ID
         *
         * Example:
         *
         * ADMIN ID = 33
         * createdBy = 33
         *
         *
         * STUDENT SELF REGISTRATION:
         *
         * createdBy = newly-created student's
         * own user ID.
         */

        if (loggedInUserId != null) {

            student.setCreatedBy(
                    loggedInUserId
            );

        } else {

            student.setCreatedBy(
                    savedUser.getId()
            );
        }


        // -----------------------------------------------------
        // DEFAULT STATUS
        // -----------------------------------------------------

        if (student.getStatus() == null ||
                student.getStatus().isBlank()) {

            student.setStatus("ACTIVE");
        }


        // -----------------------------------------------------
        // SAVE STUDENT
        // -----------------------------------------------------

        Student savedStudent =
                studentRepository.saveAndFlush(
                        student
                );


        // =====================================================
        // AUDIT LOG - CREATE STUDENT
        // =====================================================

        Long auditUserId;
        String auditEmail;
        String auditRole;

        /*
         * ADMIN creates student:
         *
         * auditUserId = ADMIN ID
         * auditEmail  = ADMIN EMAIL
         * auditRole   = ADMIN
         *
         *
         * STUDENT SELF REGISTER:
         *
         * auditUserId = STUDENT ID
         * auditEmail  = STUDENT EMAIL
         * auditRole   = STUDENT
         */

        if (loggedInUser != null) {

            auditUserId =
                    loggedInUser.getId();

            auditEmail =
                    loggedInUser.getEmail();

            auditRole =
                    loggedInUser.getRole();

        } else {

            auditUserId =
                    savedUser.getId();

            auditEmail =
                    savedUser.getEmail();

            auditRole =
                    savedUser.getRole();
        }


        /*
         * IMPORTANT:
         *
         * First 3 values = kisne action kiya
         *
         * Last 2 values = kis student par action hua
         */

        auditLogService.log(
                auditUserId,
                auditEmail,
                auditRole,
                "CREATE",
                "STUDENT",
                "Student created successfully",
                savedStudent.getId(),
                savedUser.getEmail()
        );


        return savedStudent;
    }


    // =========================================================
    // GET ALL STUDENTS
    // =========================================================

    public List<Student> getAllStudents() {

        return studentRepository
                .findByDeletedFalse();
    }


    // =========================================================
    // GET STUDENT BY ID
    // =========================================================

    public Student getStudentById(int id) {

        return studentRepository
                .findById(id)
                .orElse(null);
    }


    // =========================================================
    // GET STUDENT BY USER ID
    // =========================================================

    public Student getStudentByUserId(Long userId) {

        return studentRepository
                .findByUserIdAndDeletedFalse(userId);
    }


    // =========================================================
    // UPDATE STUDENT
    // =========================================================

    @Transactional
    public Student updateStudent(
            Student student,
            String email,
            String password) {

        Student oldStudent =
                studentRepository
                        .findById(student.getId())
                        .orElse(null);


        if (oldStudent == null) {

            return null;
        }


        // -----------------------------------------------------
        // GET ACTING USER
        // -----------------------------------------------------

        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        Long loggedInUserId =
                loggedInUser.getId();

        String auditEmail =
                loggedInUser.getEmail();

        String auditRole =
                loggedInUser.getRole();


        // -----------------------------------------------------
        // UPDATE STUDENT DETAILS
        // -----------------------------------------------------

        oldStudent.setName(
                student.getName()
        );

        oldStudent.setAge(
                student.getAge()
        );

        oldStudent.setCourse(
                student.getCourse()
        );

        oldStudent.setDepartment(
                student.getDepartment()
        );

        oldStudent.setCity(
                student.getCity()
        );

        oldStudent.setPhoneNumber(
                student.getPhoneNumber()
        );

        oldStudent.setAddress(
                student.getAddress()
        );


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        if (student.getStatus() != null &&
                !student.getStatus().isBlank()) {

            oldStudent.setStatus(
                    student.getStatus()
            );
        }


        // -----------------------------------------------------
        // GET TARGET STUDENT USER
        // -----------------------------------------------------

        User studentUser =
                oldStudent.getUser();


        String targetEmail = null;


        if (studentUser != null) {

            // -------------------------------------------------
            // UPDATE EMAIL
            // -------------------------------------------------

            if (email != null &&
                    !email.isBlank()) {

                email = email.trim();

                User existing =
                        userRepository
                                .findByEmail(email)
                                .orElse(null);


                if (existing != null &&
                        !existing.getId()
                                .equals(studentUser.getId())) {

                    throw new RuntimeException(
                            "Email already exists"
                    );
                }


                studentUser.setEmail(email);
            }


            // -------------------------------------------------
            // UPDATE PASSWORD
            // -------------------------------------------------

            if (password != null &&
                    !password.isBlank()) {

                studentUser.setPassword(
                        passwordEncoder.encode(password)
                );
            }


            userRepository.save(
                    studentUser
            );


            targetEmail =
                    studentUser.getEmail();
        }


        // -----------------------------------------------------
        // UPDATE AUDIT FIELDS
        // -----------------------------------------------------

        oldStudent.setUpdatedBy(
                loggedInUserId
        );

        oldStudent.setUpdatedDate(
                LocalDateTime.now()
        );


        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Student updatedStudent =
                studentRepository.saveAndFlush(
                        oldStudent
                );


        // =====================================================
        // AUDIT LOG - UPDATE
        // =====================================================

        /*
         * Example:
         *
         * ADMIN ne student update kiya:
         *
         * user_id = 33
         * user_email = admin@gmail.com
         * role = ADMIN
         *
         * target_student_id = 15
         * target_student_email = student@gmail.com
         */

        auditLogService.log(
                loggedInUserId,
                auditEmail,
                auditRole,
                "UPDATE",
                "STUDENT",
                "Student updated successfully",
                updatedStudent.getId(),
                targetEmail
        );


        return updatedStudent;
    }


    // =========================================================
    // DELETE STUDENT
    // =========================================================

    @Transactional
    public String deleteStudent(int id) {

        Student student =
                studentRepository
                        .findById(id)
                        .orElse(null);


        if (student == null) {

            return "Student not found";
        }


        // -----------------------------------------------------
        // GET ACTING USER
        // -----------------------------------------------------

        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        // -----------------------------------------------------
        // TARGET USER
        // -----------------------------------------------------

        User targetUser =
                student.getUser();


        String targetEmail = null;


        if (targetUser != null) {

            targetEmail =
                    targetUser.getEmail();
        }


        // -----------------------------------------------------
        // SOFT DELETE STUDENT
        // -----------------------------------------------------

        student.setDeleted(true);

        student.setStatus("INACTIVE");


        // -----------------------------------------------------
        // SOFT DELETE USER
        // -----------------------------------------------------

        if (targetUser != null) {

            targetUser.setDeleted(true);

            userRepository.save(
                    targetUser
            );
        }


        // -----------------------------------------------------
        // SAVE STUDENT
        // -----------------------------------------------------

        studentRepository.saveAndFlush(
                student
        );


        // =====================================================
        // AUDIT LOG - DELETE
        // =====================================================

        auditLogService.log(
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                loggedInUser.getRole(),
                "DELETE",
                "STUDENT",
                "Student moved to Recycle Bin",
                student.getId(),
                targetEmail
        );


        return "Student moved to Recycle Bin";
    }


    // =========================================================
    // RESTORE STUDENT
    // =========================================================

    @Transactional
    public String restoreStudent(int id) {

        Student student =
                studentRepository
                        .findById(id)
                        .orElse(null);


        if (student == null) {

            return "Student not found";
        }


        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        User targetUser =
                student.getUser();


        String targetEmail = null;


        if (targetUser != null) {

            targetEmail =
                    targetUser.getEmail();
        }


        // -----------------------------------------------------
        // RESTORE STUDENT
        // -----------------------------------------------------

        student.setDeleted(false);

        student.setStatus("ACTIVE");


        studentRepository.saveAndFlush(
                student
        );


        // -----------------------------------------------------
        // RESTORE USER
        // -----------------------------------------------------

        if (targetUser != null) {

            targetUser.setDeleted(false);

            userRepository.save(
                    targetUser
            );
        }


        // =====================================================
        // AUDIT LOG - RESTORE
        // =====================================================

        auditLogService.log(
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                loggedInUser.getRole(),
                "RESTORE",
                "STUDENT",
                "Student restored successfully",
                student.getId(),
                targetEmail
        );


        return "Student restored successfully";
    }


    // =========================================================
    // PERMANENT DELETE
    // =========================================================

    @Transactional
    public String permanentDeleteStudent(int id) {

        Student student =
                studentRepository
                        .findById(id)
                        .orElse(null);


        if (student == null) {

            return "Student not found";
        }


        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        User targetUser =
                student.getUser();


        String targetEmail = null;


        if (targetUser != null) {

            targetEmail =
                    targetUser.getEmail();
        }


        // =====================================================
        // AUDIT BEFORE DELETE
        // =====================================================

        auditLogService.log(
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                loggedInUser.getRole(),
                "PERMANENT_DELETE",
                "STUDENT",
                "Student permanently deleted",
                student.getId(),
                targetEmail
        );


        // -----------------------------------------------------
        // DELETE STUDENT
        // -----------------------------------------------------

        studentRepository.delete(
                student
        );


        // -----------------------------------------------------
        // DELETE USER
        // -----------------------------------------------------

        if (targetUser != null) {

            userRepository.delete(
                    targetUser
            );
        }


        return "Student permanently deleted";
    }


    // =========================================================
    // SEARCH BY NAME
    // =========================================================

    public List<Student> searchByName(
            String name) {

        return studentRepository
                .findByNameContainingIgnoreCaseAndDeletedFalse(
                        name
                );
    }


    // =========================================================
    // SEARCH BY DEPARTMENT
    // =========================================================

    public List<Student> searchByDepartment(
            String department) {

        return studentRepository
                .findByDepartmentContainingIgnoreCaseAndDeletedFalse(
                        department
                );
    }


    // =========================================================
    // SEARCH BY CITY
    // =========================================================

    public List<Student> searchByCity(
            String city) {

        return studentRepository
                .findByCityContainingIgnoreCaseAndDeletedFalse(
                        city
                );
    }


    // =========================================================
    // FILTER BY STATUS
    // =========================================================

    public List<Student> filterByStatus(
            String status) {

        return studentRepository
                .findByStatusAndDeletedFalse(
                        status
                );
    }


    // =========================================================
    // PAGINATION
    // =========================================================

    public Page<Student> getStudents(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return studentRepository
                .findByDeletedFalse(
                        pageable
                );
    }


    // =========================================================
    // SORT ASCENDING
    // =========================================================

    public List<Student> sortAscending() {

        return studentRepository
                .findByDeletedFalseOrderByNameAsc();
    }


    // =========================================================
    // SORT DESCENDING
    // =========================================================

    public List<Student> sortDescending() {

        return studentRepository
                .findByDeletedFalseOrderByNameDesc();
    }


    // =========================================================
    // SORT CREATED DATE
    // =========================================================

    public List<Student> sortByCreatedDate() {

        return studentRepository
                .findByDeletedFalseOrderByCreatedAtDesc();
    }


    // =========================================================
    // RECENT STUDENT
    // =========================================================

    public Student getRecentStudent() {

        return studentRepository
                .findTopByDeletedFalseOrderByCreatedAtDesc();
    }


    // =========================================================
    // UPLOAD PROFILE PHOTO
    // =========================================================

    @Transactional
    public Student uploadProfilePhoto(
            Long userId,
            MultipartFile file)
            throws IOException {

        Student student =
                studentRepository
                        .findByUserIdAndDeletedFalse(
                                userId
                        );


        if (student == null) {

            throw new RuntimeException(
                    "Student not found"
            );
        }


        if (file == null ||
                file.isEmpty()) {

            throw new RuntimeException(
                    "Please select an image"
            );
        }


        // -----------------------------------------------------
        // CHECK IMAGE TYPE
        // -----------------------------------------------------

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new RuntimeException(
                    "Only image files are allowed"
            );
        }


        // -----------------------------------------------------
        // CREATE UPLOAD DIRECTORY
        // -----------------------------------------------------

        File folder =
                new File(uploadDir);


        if (!folder.exists()) {

            folder.mkdirs();
        }


        // -----------------------------------------------------
        // GENERATE UNIQUE FILE NAME
        // -----------------------------------------------------

        String originalName =
                file.getOriginalFilename();

        if (originalName == null ||
                originalName.isBlank()) {

            originalName = "profile-image";
        }


        String fileName =
                UUID.randomUUID()
                        + "_"
                        + originalName;


        Path path =
                Paths.get(
                        uploadDir,
                        fileName
                );


        // -----------------------------------------------------
        // SAVE FILE
        // -----------------------------------------------------

        Files.copy(
                file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING
        );


        // -----------------------------------------------------
        // SAVE FILE NAME IN DATABASE
        // -----------------------------------------------------

        student.setProfilePhoto(
                fileName
        );


        return studentRepository.save(
                student
        );
    }


    // =========================================================
    // GET PROFILE
    // =========================================================

    public Student getStudentProfile(
            Long userId) {

        return studentRepository
                .findByUserIdAndDeletedFalse(
                        userId
                );
    }


    // =========================================================
    // RECYCLE BIN
    // =========================================================

    public List<Student> getDeletedStudents() {

        return studentRepository
                .findByDeletedTrueOrderByCreatedAtDesc();
    }
}