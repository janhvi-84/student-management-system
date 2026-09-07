const API_URL = "http://localhost:8081/students";

let allStudents = [];


// ========================================
// PAGE LOAD
// ========================================

window.onload = function () {

    loadStudents();

};


// ========================================
// LOAD ALL STUDENTS
// ========================================

async function loadStudents() {

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {

            throw new Error("Unable to load students");

        }

        allStudents = await response.json();


        // ========================================
        // TOTAL STUDENTS
        // ========================================

        document
            .getElementById("totalStudents")
            .innerText = allStudents.length;


        // ========================================
        // ACTIVE STUDENTS
        // ========================================

        const activeStudents =
            allStudents.filter(
                student =>
                    student.status === "ACTIVE"
            ).length;


        document
            .getElementById("activeStudents")
            .innerText = activeStudents;


        // ========================================
        // RECENT STUDENT
        // ========================================

        const recentStudent =
            allStudents
                .filter(student => student.createdAt)
                .sort(
                    (a, b) =>
                        new Date(b.createdAt) -
                        new Date(a.createdAt)
                )[0];


        document
            .getElementById("recentStudents")
            .innerText =
            recentStudent
                ? recentStudent.name
                : "0";


        // ========================================
        // STUDENT TABLE
        // ========================================

        displayStudents(allStudents);


    }

    catch (error) {

        console.error(
            "Error loading students:",
            error
        );

        alert(
            "Unable to load students"
        );

    }

}


// ========================================
// DISPLAY STUDENTS
// ========================================

function displayStudents(students) {

    const table =
        document.getElementById(
            "studentTable"
        );


    table.innerHTML = "";


    if (
        !students ||
        students.length === 0
    ) {

        table.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    style="text-align:center;"
                >

                    No Students Found

                </td>

            </tr>

        `;

        return;

    }


    students.forEach(student => {

        table.innerHTML += `

            <tr>

                <td>
                    ${student.id}
                </td>

                <td>
                    ${student.name ?? ""}
                </td>

                <td>
                    ${student.age ?? ""}
                </td>

                <td>
                    ${student.course ?? ""}
                </td>

                <td>
                    ${student.email ?? ""}
                </td>

                <td>
                    ${student.department ?? ""}
                </td>

                <td>
                    ${student.city ?? ""}
                </td>

                <td>

                    <button
                        class="edit-btn"
                        onclick="
                            editStudent(
                                ${student.id}
                            )
                        "
                    >

                        Edit

                    </button>


                    <button
                        class="delete-btn"
                        onclick="
                            deleteStudent(
                                ${student.id}
                            )
                        "
                    >

                        Delete

                    </button>

                </td>

            </tr>

        `;

    });

}


// ========================================
// EDIT STUDENT
// ========================================

function editStudent(id) {

    if (!id) {

        alert(
            "Student ID Missing"
        );

        return;

    }


    localStorage.setItem(
        "studentId",
        id
    );


    window.location.href =
        "register.html";

}


// ========================================
// DELETE STUDENT
// ========================================

async function deleteStudent(id) {

    const confirmDelete =
        confirm(
            "Are you sure you want to delete this student?"
        );


    if (!confirmDelete) {

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/" +
                id,

                {

                    method: "DELETE"

                }

            );


        if (!response.ok) {

            throw new Error(
                "Delete Failed"
            );

        }


        alert(
            "Student Deleted Successfully"
        );


        loadStudents();

    }


    catch (error) {

        console.error(
            "Delete Error:",
            error
        );

        alert(
            "Delete Failed"
        );

    }

}


// ========================================
// GLOBAL FUNCTIONS
// ========================================

window.editStudent =
    editStudent;


window.deleteStudent =
    deleteStudent;