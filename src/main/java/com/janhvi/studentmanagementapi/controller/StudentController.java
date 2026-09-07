package com.janhvi.studentmanagementapi.controller;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import org.springframework.http.MediaType;


import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.janhvi.studentmanagementapi.entity.Student;
import com.janhvi.studentmanagementapi.service.StudentService;

@RestController
@RequestMapping("/students")
@CrossOrigin
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // ========================================
    // ADD STUDENT
    // ========================================

    @PostMapping
    public ResponseEntity<?> addStudent(
            @RequestBody StudentRequest request) {

        try {

            Student student = new Student();

            student.setName(request.getName());
            student.setAge(request.getAge());
            student.setCourse(request.getCourse());
            student.setDepartment(request.getDepartment());
            student.setCity(request.getCity());
            student.setPhoneNumber(request.getPhoneNumber());
            student.setAddress(request.getAddress());
            student.setStatus(request.getStatus());

            Student savedStudent =
                    service.addStudent(
                            student,
                            request.getEmail(),
                            request.getPassword()
                    );

            return ResponseEntity.ok(savedStudent);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ========================================
    // GET ALL STUDENTS
    // ========================================

    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    // ========================================
    // GET STUDENT BY ID
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(
            @PathVariable int id) {

        Student student =
                service.getStudentById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    // ========================================
    // GET STUDENT BY USER ID
    // ========================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getStudentByUserId(
            @PathVariable Long userId) {

        Student student =
                service.getStudentByUserId(userId);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    // ========================================
    // UPDATE STUDENT
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable int id,
            @RequestBody StudentRequest request) {

        try {

            Student student = new Student();

            student.setId(id);
            student.setName(request.getName());
            student.setAge(request.getAge());
            student.setCourse(request.getCourse());
            student.setDepartment(request.getDepartment());
            student.setCity(request.getCity());
            student.setPhoneNumber(request.getPhoneNumber());
            student.setAddress(request.getAddress());
            student.setStatus(request.getStatus());

            Student updatedStudent =
                    service.updateStudent(
                            student,
                            request.getEmail(),
                            request.getPassword());

            if (updatedStudent == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedStudent);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }    // ========================================
    // DELETE STUDENT
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(
            @PathVariable int id) {

        String message =
                service.deleteStudent(id);

        if ("Student not found".equals(message)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(message);
    }

    // ========================================
    // SEARCH BY NAME
    // ========================================

    @GetMapping("/search/name")
    public List<Student> searchByName(
            @RequestParam String name) {

        return service.searchByName(name);
    }

    // ========================================
    // SEARCH BY DEPARTMENT
    // ========================================

    @GetMapping("/search/department")
    public List<Student> searchByDepartment(
            @RequestParam String department) {

        return service.searchByDepartment(department);
    }

    // ========================================
    // SEARCH BY CITY
    // ========================================

    @GetMapping("/search/city")
    public List<Student> searchByCity(
            @RequestParam String city) {

        return service.searchByCity(city);
    }

    // ========================================
    // FILTER BY STATUS
    // ========================================

    @GetMapping("/status")
    public List<Student> filterByStatus(
            @RequestParam String status) {

        return service.filterByStatus(status);
    }

    // ========================================
    // PAGINATION
    // ========================================

    @GetMapping("/page")
    public Page<Student> getStudents(
            @RequestParam int page,
            @RequestParam int size) {

        return service.getStudents(page, size);
    }

    // ========================================
    // SORT ASCENDING
    // ========================================

    @GetMapping("/sort/asc")
    public List<Student> sortAscending() {

        return service.sortAscending();
    }

    // ========================================
    // SORT DESCENDING
    // ========================================

    @GetMapping("/sort/desc")
    public List<Student> sortDescending() {

        return service.sortDescending();
    }

    // ========================================
    // SORT BY CREATED DATE
    // ========================================

    @GetMapping("/sort/created")
    public List<Student> sortByCreatedDate() {

        return service.sortByCreatedDate();
    }

    // ========================================
    // RECENT STUDENT
    // ========================================

    @GetMapping("/recent")
    public Student getRecentStudent() {

        return service.getRecentStudent();
    }


    // ========================================
// UPLOAD PROFILE PHOTO
// ========================================

@PostMapping(
        value = "/upload-photo/{userId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<?> uploadPhoto(

        @PathVariable Long userId,

        @RequestParam("file") MultipartFile file) {

    try {

        Student student =
                service.uploadProfilePhoto(userId, file);

        return ResponseEntity.ok(student);

    } catch (Exception e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
// ========================================
// GET PROFILE
// ========================================

@GetMapping("/profile/{userId}")
public ResponseEntity<?> getProfile(

        @PathVariable Long userId) {

    Student student =
            service.getStudentProfile(userId);

    if (student == null) {

        return ResponseEntity.notFound().build();

    }

    return ResponseEntity.ok(student);

}





// ========================================
// RECYCLE BIN
// ========================================

@GetMapping("/recycle-bin")
public ResponseEntity<?> getDeletedStudents() {

    return ResponseEntity.ok(
            service.getDeletedStudents()
    );
}


// ========================================
// RESTORE STUDENT
// ========================================

@PutMapping("/restore/{id}")
public ResponseEntity<?> restoreStudent(
        @PathVariable int id) {

    return ResponseEntity.ok(
            service.restoreStudent(id)
    );
}

// ========================================
// PERMANENT DELETE
// ========================================

@DeleteMapping("/permanent/{id}")
public ResponseEntity<?> permanentDeleteStudent(
        @PathVariable int id) {

    return ResponseEntity.ok(
            service.permanentDeleteStudent(id)
    );
}

    // ========================================
    // STUDENT REQUEST DTO
    // ========================================

    public static class StudentRequest {

        private String name;
        private int age;
        private String course;
        private String email;
        private String department;
        private String city;
        private String phoneNumber;
        private String address;
        private String password;
        private String status;

        public StudentRequest() {
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}