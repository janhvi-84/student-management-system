// ========================================
// STUDENT MANAGEMENT
// ========================================

const API_URL =
    "http://localhost:8081/students";


let currentPage = 0;

let pageSize = 5;

let totalPages = 0;


// ========================================
// PAGE LOAD
// ========================================

window.onload = function () {

    if (!checkAdminAccess()) {

        return;

    }


    registerEvents();

    loadPage(0);

};


// ========================================
// CHECK ADMIN ACCESS
// ========================================

function checkAdminAccess() {

    const token =
        localStorage.getItem(
            "token"
        );


    const loggedInUser =
        JSON.parse(
            localStorage.getItem(
                "loggedInUser"
            )
        );


    if (
        !token ||
        !loggedInUser
    ) {

        alert(
            "Please login first."
        );


        window.location.href =
            "login.html";


        return false;

    }


    if (
        loggedInUser.role !==
        "ADMIN"
    ) {

        alert(
            "Access Denied. Only Admin can access this page."
        );


        window.location.href =
            "student-dashboard.html";


        return false;

    }


    return true;

}


// ========================================
// AUTH HEADERS
// ========================================

function getAuthHeaders() {

    const token =
        localStorage.getItem(
            "token"
        );


    return {

        "Content-Type":
            "application/json",


        "Authorization":
            "Bearer " + token

    };

}


// ========================================
// REGISTER EVENTS
// ========================================

function registerEvents() {


    document
        .getElementById(
            "searchBtn"
        )
        .addEventListener(
            "click",
            searchStudent
        );


    document
        .getElementById(
            "statusFilter"
        )
        .addEventListener(
            "change",
            filterStatus
        );


    document
        .getElementById(
            "sortOption"
        )
        .addEventListener(
            "change",
            sortStudents
        );


    document
        .getElementById(
            "prevPage"
        )
        .addEventListener(
            "click",
            previousPage
        );


    document
        .getElementById(
            "nextPage"
        )
        .addEventListener(
            "click",
            nextPage
        );


    document
        .getElementById(
            "addStudent"
        )
        .addEventListener(
            "click",
            function () {

                localStorage.removeItem(
                    "studentId"
                );


                window.location.href =
                    "register.html";

            }

        );

}


// ========================================
// LOAD PAGINATED STUDENTS
// ========================================

async function loadPage(
    page
) {

    try {

        const response =
            await fetch(

                `${API_URL}/page?page=${page}&size=${pageSize}`,

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (
            !response.ok
        ) {

            throw new Error(
                "Unable to load students"
            );

        }


        const data =
            await response.json();


        currentPage =
            data.number;


        totalPages =
            data.totalPages;


        displayStudents(
            data.content
        );


        document
            .getElementById(
                "pageNumber"
            )
            .innerText =

            totalPages === 0

                ? "No Pages"

                : `Page ${
                    currentPage + 1
                  } of ${
                    totalPages
                  }`;


        document
            .getElementById(
                "prevPage"
            )
            .disabled =

            currentPage === 0;


        document
            .getElementById(
                "nextPage"
            )
            .disabled =

            totalPages === 0 ||
            currentPage >=
            totalPages - 1;

    }


    catch (error) {

        console.error(
            "Load Students Error:",
            error
        );


        alert(
            "Unable to Load Students"
        );

    }

}


// ========================================
// DISPLAY STUDENTS
// ========================================

function displayStudents(
    students
) {

    const table =
        document
            .getElementById(
                "studentTable"
            );


    table.innerHTML =
        "";


    if (

        !students ||

        students.length === 0

    ) {

        table.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    style="
                        text-align:center;
                        padding:30px;
                    "
                >

                    No Students Found

                </td>

            </tr>

        `;


        return;

    }


    students.forEach(
        student => {

            table.innerHTML += `

                <tr>

                    <td>
                        ${student.id ?? ""}
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

        }

    );

}


// ========================================
// SEARCH STUDENT
// ========================================

async function searchStudent() {

    const name =
        document
            .getElementById(
                "search"
            )
            .value
            .trim();


    if (
        name === ""
    ) {

        loadPage(0);

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/search/name?name=" +
                encodeURIComponent(
                    name
                ),

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (
            !response.ok
        ) {

            throw new Error(
                "Search Failed"
            );

        }


        const students =
            await response.json();


        displayStudents(
            students
        );


        document
            .getElementById(
                "pageNumber"
            )
            .innerText =
            "Search Result";


        disablePagination();

    }


    catch (error) {

        console.error(
            "Search Error:",
            error
        );


        alert(
            "Search Failed"
        );

    }

}


// ========================================
// FILTER STATUS
// ========================================

async function filterStatus() {

    const status =
        document
            .getElementById(
                "statusFilter"
            )
            .value;


    if (
        status === ""
    ) {

        loadPage(0);

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/status?status=" +
                encodeURIComponent(
                    status
                ),

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (
            !response.ok
        ) {

            throw new Error(
                "Filter Failed"
            );

        }


        const students =
            await response.json();


        displayStudents(
            students
        );


        document
            .getElementById(
                "pageNumber"
            )
            .innerText =
            status +
            " Students";


        disablePagination();

    }


    catch (error) {

        console.error(
            "Filter Error:",
            error
        );


        alert(
            "Filter Failed"
        );

    }

}


// ========================================
// SORT STUDENTS
// ========================================

async function sortStudents() {

    const sort =
        document
            .getElementById(
                "sortOption"
            )
            .value;


    if (
        sort === ""
    ) {

        loadPage(0);

        return;

    }


    let url =
        API_URL;


    if (
        sort === "asc"
    ) {

        url +=
            "/sort/asc";

    }


    else if (
        sort === "desc"
    ) {

        url +=
            "/sort/desc";

    }


    else if (
        sort === "created"
    ) {

        url +=
            "/sort/created";

    }


    try {

        const response =
            await fetch(

                url,

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (
            !response.ok
        ) {

            throw new Error(
                "Sort Failed"
            );

        }


        const students =
            await response.json();


        displayStudents(
            students
        );


        document
            .getElementById(
                "pageNumber"
            )
            .innerText =
            "Sorted Data";


        disablePagination();

    }


    catch (error) {

        console.error(
            "Sort Error:",
            error
        );


        alert(
            "Sorting Failed"
        );

    }

}


// ========================================
// DISABLE PAGINATION
// ========================================

function disablePagination() {

    document
        .getElementById(
            "prevPage"
        )
        .disabled =
        true;


    document
        .getElementById(
            "nextPage"
        )
        .disabled =
        true;

}


// ========================================
// PREVIOUS PAGE
// ========================================

function previousPage() {

    if (
        currentPage > 0
    ) {

        loadPage(
            currentPage - 1
        );

    }

}


// ========================================
// NEXT PAGE
// ========================================

function nextPage() {

    if (

        currentPage <
        totalPages - 1

    ) {

        loadPage(
            currentPage + 1
        );

    }

}


// ========================================
// DELETE STUDENT
// ========================================

async function deleteStudent(
    id
) {

    const confirmDelete =
        confirm(
            "Are you sure you want to delete this student?"
        );


    if (
        !confirmDelete
    ) {

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/" +
                id,

                {

                    method:
                        "DELETE",


                    headers:
                        getAuthHeaders()

                }

            );


        if (
            !response.ok
        ) {

            throw new Error(
                "Delete Failed"
            );

        }


        alert(
            "Student Deleted Successfully"
        );


        loadPage(
            currentPage
        );

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
// EDIT STUDENT
// ========================================

function editStudent(
    id
) {

    if (
        !id
    ) {

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
// LOGOUT
// ========================================

function logout() {

    localStorage.removeItem(
        "studentId"
    );


    localStorage.removeItem(
        "token"
    );


    localStorage.removeItem(
        "loggedInUser"
    );


    window.location.href =
        "login.html";

}


// ========================================
// GLOBAL FUNCTIONS
// ========================================

window.editStudent =
    editStudent;


window.deleteStudent =
    deleteStudent;


window.logout =
    logout;