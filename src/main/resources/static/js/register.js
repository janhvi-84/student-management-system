// ========================================
// API URLS
// ========================================

const STUDENT_API_URL =
    "http://localhost:8081/students";

const AUTH_API_URL =
    "http://localhost:8081/auth";


// ========================================
// STUDENT ID
// ========================================

const studentId =
    localStorage.getItem("studentId");


// ========================================
// PAGE LOAD
// ========================================

window.onload = async function () {

    // ========================================
    // NEW REGISTRATION
    // ========================================

    if (!studentId) {

        console.log(
            "NEW STUDENT REGISTRATION MODE"
        );

        return;
    }


    // ========================================
    // UPDATE MODE
    // ========================================

    console.log(
        "UPDATE STUDENT ID =",
        studentId
    );


    document
        .querySelector("h2")
        .innerText =
        "Update Student";


    document
        .querySelector(
            "button[type='submit']"
        )
        .innerText =
        "Update";


    try {

        // ========================================
        // LOAD STUDENT
        // ========================================

        const response =
            await fetch(

                STUDENT_API_URL +
                "/" +
                studentId,

                {

                    headers: {

                        "Authorization":
                            "Bearer " +
                            localStorage.getItem(
                                "token"
                            )

                    }

                }

            );


        if (!response.ok) {

            throw new Error(
                "Student not found"
            );
        }


        const student =
            await response.json();


        // ========================================
        // STUDENT DETAILS
        // ========================================

        document
            .getElementById("name")
            .value =
            student.name || "";


        document
            .getElementById("age")
            .value =
            student.age || "";


        document
            .getElementById("course")
            .value =
            student.course || "";


        document
            .getElementById("department")
            .value =
            student.department || "";


        document
            .getElementById("city")
            .value =
            student.city || "";


        document
            .getElementById("phoneNumber")
            .value =
            student.phoneNumber || "";


        document
            .getElementById("address")
            .value =
            student.address || "";


        // ========================================
        // EMAIL
        // ========================================

        if (student.user) {

            document
                .getElementById("email")
                .value =
                student.user.email || "";

        }


        // ========================================
        // PASSWORD
        // ========================================

        document
            .getElementById("password")
            .required =
            false;


        document
            .getElementById("password")
            .placeholder =
            "Leave blank to keep old password";


    } catch (error) {

        console.error(
            "Load Student Error:",
            error
        );


        alert(
            "Unable to load student."
        );

    }

};


// ========================================
// REGISTRATION FORM
// ========================================

document
    .getElementById("registerForm")
    .addEventListener(
        "submit",
        registerStudent
    );


// ========================================
// REGISTER / UPDATE STUDENT
// ========================================

async function registerStudent(event) {

    event.preventDefault();


    // ========================================
    // CHECK ADMIN CREATE MODE
    // ========================================

    const adminAddingStudent =
        localStorage.getItem(
            "adminAddingStudent"
        );


    const isAdminCreatingStudent =
        adminAddingStudent === "true";


    console.log(
        "ADMIN CREATE MODE =",
        isAdminCreatingStudent
    );


    // ========================================
    // GET VALUES
    // ========================================

    const name =
        document
            .getElementById("name")
            .value
            .trim();


    const age =
        parseInt(
            document
                .getElementById("age")
                .value
        );


    const course =
        document
            .getElementById("course")
            .value
            .trim();


    const email =
        document
            .getElementById("email")
            .value
            .trim();


    const department =
        document
            .getElementById("department")
            .value
            .trim();


    const city =
        document
            .getElementById("city")
            .value
            .trim();


    const phoneNumber =
        document
            .getElementById("phoneNumber")
            .value
            .trim();


    const address =
        document
            .getElementById("address")
            .value
            .trim();


    const password =
        document
            .getElementById("password")
            .value;


    // ========================================
    // VALIDATION
    // ========================================

    if (

        name === "" ||

        isNaN(age) ||

        age <= 0 ||

        course === "" ||

        email === "" ||

        department === "" ||

        city === "" ||

        phoneNumber === "" ||

        address === "" ||

        (!studentId && password === "")

    ) {

        alert(
            "Please fill all fields."
        );

        return;
    }


    // ========================================
    // REQUEST DATA
    // ========================================

    const student = {

        name: name,

        age: age,

        course: course,

        email: email,

        department: department,

        city: city,

        phoneNumber: phoneNumber,

        address: address,

        password: password

    };


    console.log(
        "REQUEST DATA =",
        student
    );


    try {

        let response;


        // ========================================
        // UPDATE STUDENT
        // ========================================

        if (studentId) {

            console.log(
                "UPDATE STUDENT MODE"
            );


            response =
                await fetch(

                    STUDENT_API_URL +
                    "/" +
                    studentId,

                    {

                        method: "PUT",

                        headers: {

                            "Content-Type":
                                "application/json",

                            "Authorization":
                                "Bearer " +
                                localStorage.getItem(
                                    "token"
                                )

                        },

                        body:
                            JSON.stringify(
                                student
                            )

                    }

                );

        }


        // ========================================
        // NEW STUDENT REGISTRATION
        // ========================================

        else {

            // ========================================
            // ADMIN CREATE STUDENT
            // ========================================

            if (
                isAdminCreatingStudent
            ) {

                console.log(
                    "ADMIN CREATE STUDENT MODE"
                );


                const token =
                    localStorage.getItem(
                        "token"
                    );


                if (!token) {

                    alert(
                        "Admin session expired. Please login again."
                    );

                    window.location.href =
                        "login.html";

                    return;
                }


                response =
                    await fetch(

                        STUDENT_API_URL,

                        {

                            method: "POST",

                            headers: {

                                "Content-Type":
                                    "application/json",

                                "Authorization":
                                    "Bearer " +
                                    token

                            },

                            body:
                                JSON.stringify(
                                    student
                                )

                        }

                    );

            }


            // ========================================
            // NORMAL STUDENT REGISTRATION
            // ========================================

            else {

                console.log(
                    "NORMAL STUDENT REGISTRATION MODE"
                );


                response =
                    await fetch(

                        AUTH_API_URL +
                        "/register",

                        {

                            method: "POST",

                            headers: {

                                "Content-Type":
                                    "application/json"

                            },

                            body:
                                JSON.stringify(
                                    student
                                )

                        }

                    );

            }

        }


        // ========================================
        // READ RESPONSE
        // ========================================

        const responseText =
            await response.text();


        console.log(
            "BACKEND RESPONSE =",
            responseText
        );


        // ========================================
        // ERROR
        // ========================================

        if (!response.ok) {

            console.error(
                "BACKEND ERROR =",
                responseText
            );


            alert(
                responseText ||
                "Operation Failed"
            );


            return;
        }


        // ========================================
        // UPDATE SUCCESS
        // ========================================

        if (studentId) {

            alert(
                "Student Updated Successfully"
            );


            localStorage.removeItem(
                "studentId"
            );


            // ========================================
            // ADMIN UPDATE
            // ========================================
            // Admin ka token aur loggedInUser
            // remove nahi karna hai.

            if (
                localStorage.getItem(
                    "token"
                ) &&
                JSON.parse(
                    localStorage.getItem(
                        "loggedInUser"
                    ) || "null"
                )?.role === "ADMIN"
            ) {

                window.location.href =
                    "dashboard.html";

            }

            // ========================================
            // NORMAL UPDATE FALLBACK
            // ========================================

            else {

                window.location.href =
                    "dashboard.html";

            }


            return;
        }


        // ========================================
        // REGISTRATION RESPONSE
        // ========================================

        let result;


        try {

            result =
                JSON.parse(
                    responseText
                );

        } catch (error) {

            console.error(
                "Invalid JSON Response:",
                error
            );


            alert(
                "Registration response invalid."
            );


            return;
        }


        console.log(
            "REGISTRATION RESULT =",
            result
        );


        // ========================================
        // GET GENERATED IDs
        // ========================================

        let userId;

        let registeredStudentId;


        // ========================================
        // ADMIN CREATE RESPONSE
        // ========================================

        if (
            isAdminCreatingStudent
        ) {

            // ----------------------------------------
            // /students POST returns Student object
            // ----------------------------------------

            registeredStudentId =
                result.id;


            userId =
                result.user
                    ? result.user.id
                    : null;


            console.log(
                "ADMIN CREATED STUDENT"
            );

        }


        // ========================================
        // NORMAL REGISTER RESPONSE
        // ========================================

        else {

            // ----------------------------------------
            // /auth/register returns:
            // userId
            // studentId
            // ----------------------------------------

            userId =
                result.userId;


            registeredStudentId =
                result.studentId;

        }


        // ========================================
        // LOG GENERATED IDs
        // ========================================

        console.log(
            "GENERATED USER ID =",
            userId
        );


        console.log(
            "GENERATED STUDENT ID =",
            registeredStudentId
        );


        // ========================================
        // CHECK IDS
        // ========================================

        if (

            userId === null ||

            userId === undefined ||

            registeredStudentId === null ||

            registeredStudentId === undefined

        ) {

            console.error(
                "ID NOT RECEIVED:",
                result
            );


            alert(
                "Student create ho gaya, lekin ID response mein nahi aayi."
            );


            return;
        }


        // ========================================
        // STORE GENERATED IDs
        // ========================================

        localStorage.setItem(
            "registeredUserId",
            String(userId)
        );


        localStorage.setItem(
            "registeredStudentId",
            String(registeredStudentId)
        );


        // ========================================
        // SUCCESS MESSAGE
        // ========================================

        alert(

            "Student Registered Successfully\n\n" +

            "User ID: " +
            userId +

            "\nStudent ID: " +
            registeredStudentId

        );


        // ========================================
        // ADMIN CREATED STUDENT
        // ========================================

        if (
            isAdminCreatingStudent
        ) {

            console.log(
                "Admin created student successfully."
            );


            // ----------------------------------------
            // REMOVE ADMIN CREATE FLAG
            // ----------------------------------------

            localStorage.removeItem(
                "adminAddingStudent"
            );


            // ----------------------------------------
            // STUDENT ID CLEANUP
            // ----------------------------------------

            localStorage.removeItem(
                "studentId"
            );


            // ----------------------------------------
            // IMPORTANT
            // ----------------------------------------
            // Admin ka:
            //
            // token
            // loggedInUser
            //
            // remove NAHI karna hai.
            //
            // Isliye Admin ko dobara login
            // nahi karna padega.

            console.log(
                "Returning to Admin Dashboard..."
            );


            window.location.href =
                "dashboard.html";


            return;
        }


        // ========================================
        // NORMAL STUDENT REGISTRATION
        // ========================================

        localStorage.removeItem(
            "adminAddingStudent"
        );


        // ----------------------------------------
        // Normal student ko login karna hoga
        // ----------------------------------------

        window.location.href =
            "login.html";


    } catch (error) {

        console.error(
            "Registration Error:",
            error
        );


        alert(
            "Server se connection nahi ho pa raha hai."
        );

    }

}