// ========================================
// ADMIN DASHBOARD - STUDENT MANAGEMENT
// ========================================


// ========================================
// API CONFIGURATION
// ========================================

const API_URL =
    "http://localhost:8081/students";

const AUDIT_API_URL =
    "http://localhost:8081/audit-logs";

const ADMIN_API_URL =
    "http://localhost:8081/admin";

const ADMIN_PHOTO_KEY =
    "adminProfilePhoto";


// ========================================
// GLOBAL VARIABLES
// ========================================

let allStudents = [];

let currentPage = 0;

let pageSize = 5;

let totalPages = 0;

let chart = null;

let allAuditLogs = [];

window.currentAdmin = null;


// ========================================
// SAFE ELEMENT GETTER
// ========================================

function getElement(id) {

    return document.getElementById(id);

}


// ========================================
// PAGE LOAD
// ========================================

window.onload = async function () {

    if (!checkAdminAccess()) {

        return;

    }


    initializeDashboard();


    await loadAdminProfile();


    initializeAdminPhotoEvent();

};


// ========================================
// CHECK ADMIN ACCESS
// ========================================

function checkAdminAccess() {

    const token =
        localStorage.getItem("token");


    let loggedInUser = null;


    try {

        loggedInUser =
            JSON.parse(
                localStorage.getItem(
                    "loggedInUser"
                )
            );

    }

    catch (error) {

        console.error(
            "Invalid loggedInUser:",
            error
        );

    }


    if (!token || !loggedInUser) {

        alert(
            "Please login first"
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
// GET LOGGED IN USER
// ========================================

function getLoggedInUser() {

    try {

        return JSON.parse(
            localStorage.getItem(
                "loggedInUser"
            )
        );

    }

    catch (error) {

        console.error(
            "Unable to read loggedInUser:",
            error
        );


        return null;

    }

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
// HIDE ALL SECTIONS
// ========================================

function hideAllSections() {

    const sections = [

        "dashboardSection",

        "studentsSection",

        "recycleBinSection",

        "adminProfileSection",

        "adminChangePasswordSection",

        "auditLogsSection"

    ];


    sections.forEach(

        function (sectionId) {

            const section =
                getElement(
                    sectionId
                );


            if (section) {

                section.style.display =
                    "none";

            }

        }

    );

}


// ========================================
// INITIALIZE DASHBOARD
// ========================================

function initializeDashboard() {

    initializeChart();

    registerEvents();

    loadStudents();

    loadRecentStudent();

}


// ========================================
// SAFE EVENT REGISTER
// ========================================

function addClickEvent(
    id,
    handler
) {

    const element =
        getElement(id);


    if (element) {

        element.addEventListener(
            "click",
            handler
        );

    }

}


function addChangeEvent(
    id,
    handler
) {

    const element =
        getElement(id);


    if (element) {

        element.addEventListener(
            "change",
            handler
        );

    }

}


// ========================================
// REGISTER EVENTS
// ========================================

function registerEvents() {


    addClickEvent(

        "dashboardMenu",

        showDashboard

    );


    addClickEvent(

        "studentMenu",

        showStudents

    );


    addClickEvent(

        "totalCard",

        function () {

            showStudents();

            loadStudents();

        }

    );


    addClickEvent(

        "activeCard",

        function () {

            showStudents();


            const statusFilter =
                getElement(
                    "statusFilter"
                );


            if (statusFilter) {

                statusFilter.value =
                    "ACTIVE";

            }


            filterStatus();

        }

    );


    addClickEvent(

        "recentCard",

        showRecentStudent

    );


    addClickEvent(

        "quickAdd",

        addStudent

    );


    addClickEvent(

        "quickView",

        showStudents

    );


    addClickEvent(

        "quickRefresh",

        refreshDashboard

    );


    addClickEvent(

        "quickAdmin",

        function () {

            window.location.href =
                "admin-management.html";

        }

    );


    addClickEvent(

        "addStudent",

        addStudent

    );


    addClickEvent(

        "searchBtn",

        searchStudent

    );


    addChangeEvent(

        "statusFilter",

        filterStatus

    );


    addChangeEvent(

        "sortOption",

        sortStudents

    );


    addClickEvent(

        "prevPage",

        previousPage

    );


    addClickEvent(

        "nextPage",

        nextPage

    );

}


// ========================================
// ADD STUDENT
// ========================================

function addStudent() {

    localStorage.removeItem(
        "studentId"
    );


    localStorage.setItem(

        "adminAddingStudent",

        "true"

    );


    window.location.href =
        "register.html";

}


// ========================================
// SHOW DASHBOARD
// ========================================

function showDashboard() {

    hideAllSections();


    const section =
        getElement(
            "dashboardSection"
        );


    if (section) {

        section.style.display =
            "block";

    }


    loadStudents();

    loadRecentStudent();

}


// ========================================
// SHOW STUDENTS
// ========================================

function showStudents() {

    hideAllSections();


    const section =
        getElement(
            "studentsSection"
        );


    if (section) {

        section.style.display =
            "block";

    }

}


// ========================================
// INITIALIZE CHART
// ========================================

function initializeChart() {

    const canvas =
        getElement(
            "studentChart"
        );


    if (!canvas) {

        return;

    }


    if (
        typeof Chart ===
        "undefined"
    ) {

        console.warn(
            "Chart.js not loaded"
        );


        return;

    }


    const ctx =
        canvas.getContext(
            "2d"
        );


    chart =
        new Chart(

            ctx,

            {

                type:
                    "bar",


                data:
                    {

                        labels:
                            [

                                "Total Students",

                                "Active Students"

                            ],


                        datasets:
                            [

                                {

                                    label:
                                        "Students",


                                    data:
                                        [

                                            0,

                                            0

                                        ],


                                    borderWidth:
                                        1

                                }

                            ]

                    },


                options:
                    {

                        responsive:
                            true,


                        maintainAspectRatio:
                            false

                    }

            }

        );

}


// ========================================
// UPDATE CHART
// ========================================

function updateChart() {

    if (!chart) {

        return;

    }


    const activeStudents =
        allStudents.filter(

            student =>

                student.status ===
                "ACTIVE"

        ).length;


    chart
        .data
        .datasets[0]
        .data =

        [

            allStudents.length,

            activeStudents

        ];


    chart.update();

}


// ========================================
// LOAD ALL STUDENTS
// ========================================

async function loadStudents() {

    try {

        const response =
            await fetch(

                API_URL,

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Unable to load students"
            );

        }


        allStudents =
            await response.json();


        const totalElement =
            getElement(
                "totalStudents"
            );


        if (totalElement) {

            totalElement.innerText =
                allStudents.length;

        }


        const activeStudents =
            allStudents.filter(

                student =>

                    student.status ===
                    "ACTIVE"

            ).length;


        const activeElement =
            getElement(
                "activeStudents"
            );


        if (activeElement) {

            activeElement.innerText =
                activeStudents;

        }


        updateChart();


        loadPage(
            0
        );

    }


    catch (error) {

        console.error(
            "Load Students Error:",
            error
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
        getElement(
            "studentTable"
        );


    if (!table) {

        return;

    }


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
                        ${student.user?.email ?? ""}
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
// LOAD RECENT STUDENT
// ========================================

async function loadRecentStudent() {

    const recentElement =
        getElement(
            "recentStudents"
        );


    try {

        const response =
            await fetch(

                API_URL +
                "/recent",

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            if (recentElement) {

                recentElement.innerText =
                    "0";

            }


            return;

        }


        const student =
            await response.json();


        if (recentElement) {

            recentElement.innerText =

                student &&
                student.name

                    ? student.name

                    : "0";

        }

    }


    catch (error) {

        console.error(
            "Recent Student Error:",
            error
        );

    }

}


// ========================================
// SHOW RECENT STUDENT
// ========================================

async function showRecentStudent() {

    showStudents();


    try {

        const response =
            await fetch(

                API_URL +
                "/recent",

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Recent student load failed"
            );

        }


        const student =
            await response.json();


        displayStudents(

            student
                ? [student]
                : []

        );


        const pageNumber =
            getElement(
                "pageNumber"
            );


        if (pageNumber) {

            pageNumber.innerText =
                "Recent Student";

        }

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            "Recent student load nahi ho raha."
        );

    }

}


// ========================================
// SEARCH STUDENT
// ========================================

async function searchStudent() {

    const searchElement =
        getElement(
            "search"
        );


    const name =
        searchElement
            ? searchElement.value.trim()
            : "";


    if (name === "") {

        loadStudents();

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


        if (!response.ok) {

            throw new Error(
                "Search failed"
            );

        }


        const students =
            await response.json();


        showStudents();


        displayStudents(
            students
        );


        const pageNumber =
            getElement(
                "pageNumber"
            );


        if (pageNumber) {

            pageNumber.innerText =
                "Search Result";

        }

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            "Search failed"
        );

    }

}


// ========================================
// FILTER STATUS
// ========================================

async function filterStatus() {

    const filter =
        getElement(
            "statusFilter"
        );


    const status =
        filter
            ? filter.value
            : "";


    if (status === "") {

        loadStudents();

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/status?status=" +
                status,

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Filter failed"
            );

        }


        const students =
            await response.json();


        showStudents();


        displayStudents(
            students
        );


        const pageNumber =
            getElement(
                "pageNumber"
            );


        if (pageNumber) {

            pageNumber.innerText =
                "Filtered Result";

        }

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            "Filter failed"
        );

    }

}


// ========================================
// SORT STUDENTS
// ========================================

async function sortStudents() {

    const sortElement =
        getElement(
            "sortOption"
        );


    const sort =
        sortElement
            ? sortElement.value
            : "";


    if (sort === "") {

        loadStudents();

        return;

    }


    let url =
        API_URL;


    if (sort === "asc") {

        url +=
            "/sort/asc";

    }


    else if (sort === "desc") {

        url +=
            "/sort/desc";

    }


    else if (sort === "created") {

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


        if (!response.ok) {

            throw new Error(
                "Sort failed"
            );

        }


        const students =
            await response.json();


        showStudents();


        displayStudents(
            students
        );


        const pageNumber =
            getElement(
                "pageNumber"
            );


        if (pageNumber) {

            pageNumber.innerText =
                "Sorted Result";

        }

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            "Sorting failed"
        );

    }

}


// ========================================
// PAGINATION
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


        if (!response.ok) {

            throw new Error(
                "Pagination failed"
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


        const pageNumber =
            getElement(
                "pageNumber"
            );


        if (pageNumber) {

            pageNumber.innerText =

                `Page ${
                    currentPage + 1
                } of ${
                    totalPages
                }`;

        }


        const prevPage =
            getElement(
                "prevPage"
            );


        const nextPage =
            getElement(
                "nextPage"
            );


        if (prevPage) {

            prevPage.disabled =
                currentPage === 0;

        }


        if (nextPage) {

            nextPage.disabled =

                currentPage >=
                totalPages - 1;

        }

    }


    catch (error) {

        console.error(
            "Pagination Error:",
            error
        );

    }

}


// ========================================
// PREVIOUS PAGE
// ========================================

function previousPage() {

    if (
        currentPage >
        0
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
// REFRESH DASHBOARD
// ========================================

function refreshDashboard() {

    const search =
        getElement(
            "search"
        );


    const statusFilter =
        getElement(
            "statusFilter"
        );


    const sortOption =
        getElement(
            "sortOption"
        );


    if (search) {

        search.value =
            "";

    }


    if (statusFilter) {

        statusFilter.value =
            "";

    }


    if (sortOption) {

        sortOption.value =
            "";

    }


    loadStudents();

    loadRecentStudent();

}


// ========================================
// EDIT STUDENT
// ========================================

function editStudent(
    id
) {

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

async function deleteStudent(
    id
) {

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

                    method:
                        "DELETE",

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Delete failed"
            );

        }


        alert(
            "Student Deleted Successfully"
        );


        loadStudents();

        loadRecentStudent();

    }


    catch (error) {

        console.error(
            "Delete Error:",
            error
        );


        alert(
            "Delete failed"
        );

    }

}


// ========================================
// ADMIN PROFILE
// ========================================

async function showAdminProfile() {

    hideAllSections();


    const section =
        getElement(
            "adminProfileSection"
        );


    if (section) {

        section.style.display =
            "block";

    }


    await loadAdminProfile();

}


window.showAdminProfile =
    showAdminProfile;


// ========================================
// HIDE ADMIN PROFILE
// ========================================

function hideAdminProfile() {

    showDashboard();

}


window.hideAdminProfile =
    hideAdminProfile;


// ========================================
// RESTORE ADMIN PHOTO
// ========================================

function restoreAdminPhoto() {

    const photoElement =
        getElement(
            "adminProfilePhoto"
        );


    if (!photoElement) {

        return;

    }


    const savedPhoto =
        localStorage.getItem(
            ADMIN_PHOTO_KEY
        );


    if (savedPhoto) {

        photoElement.src =
            savedPhoto;

    }

}


// ========================================
// LOAD ADMIN PROFILE
// ========================================

async function loadAdminProfile() {

    const loggedInUser =
        getLoggedInUser();


    if (
        !loggedInUser ||
        !loggedInUser.id
    ) {

        return;

    }


    try {

        const response =
            await fetch(

                ADMIN_API_URL +
                "/" +
                loggedInUser.id,

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Unable to load admin profile"
            );

        }


        const admin =
            await response.json();


        window.currentAdmin =
            admin;


        const updatedLoggedInUser = {

            ...loggedInUser,

            name:
                admin.name ||
                loggedInUser.name ||
                "Admin",

            email:
                admin.email ||
                loggedInUser.email,

            phoneNumber:
                admin.phoneNumber ||
                loggedInUser.phoneNumber ||
                "",

            address:
                admin.address ||
                loggedInUser.address ||
                ""

        };


        localStorage.setItem(

            "loggedInUser",

            JSON.stringify(
                updatedLoggedInUser
            )

        );


        updateAdminProfileUI(
            updatedLoggedInUser
        );


        // IMPORTANT:
        // Backend profile reload photo ko overwrite nahi karega
        restoreAdminPhoto();

    }


    catch (error) {

        console.error(

            "Load Admin Profile Error:",

            error

        );


        // Backend error ho tab bhi
        // local photo remove nahi hogi

        restoreAdminPhoto();

    }

}


// ========================================
// UPDATE ADMIN PROFILE UI
// ========================================

function updateAdminProfileUI(
    admin
) {

    const adminName =
        getElement(
            "adminName"
        );


    const adminProfileName =
        getElement(
            "adminProfileName"
        );


    const adminNameView =
        getElement(
            "adminNameView"
        );


    const adminEmailView =
        getElement(
            "adminEmailView"
        );


    const adminPhoneView =
        getElement(
            "adminPhoneView"
        );


    const adminAddressView =
        getElement(
            "adminAddressView"
        );


    if (adminName) {

        adminName.innerText =
            admin.name ||
            "Admin";

    }


    if (adminProfileName) {

        adminProfileName.innerText =
            admin.name ||
            "Admin";

    }


    if (adminNameView) {

        adminNameView.innerText =
            admin.name ||
            "-";

    }


    if (adminEmailView) {

        adminEmailView.innerText =
            admin.email ||
            "-";

    }


    if (adminPhoneView) {

        adminPhoneView.innerText =
            admin.phoneNumber ||
            "-";

    }


    if (adminAddressView) {

        adminAddressView.innerText =
            admin.address ||
            "-";

    }

}


// ========================================
// OPEN ADMIN EDIT MODAL
// ========================================

async function openAdminEditModal() {

    if (!window.currentAdmin) {

        await loadAdminProfile();

    }


    if (!window.currentAdmin) {

        alert(
            "Admin profile data not available."
        );

        return;

    }


    const name =
        getElement(
            "editAdminName"
        );


    const phone =
        getElement(
            "editAdminPhone"
        );


    const address =
        getElement(
            "editAdminAddress"
        );


    const modal =
        getElement(
            "adminEditModal"
        );


    if (name) {

        name.value =
            window.currentAdmin.name ||
            "";

    }


    if (phone) {

        phone.value =
            window.currentAdmin.phoneNumber ||
            "";

    }


    if (address) {

        address.value =
            window.currentAdmin.address ||
            "";

    }


    if (modal) {

        modal.style.display =
            "flex";

    }

}


window.openAdminEditModal =
    openAdminEditModal;


// ========================================
// CLOSE ADMIN EDIT MODAL
// ========================================

function closeAdminEditModal() {

    const modal =
        getElement(
            "adminEditModal"
        );


    if (modal) {

        modal.style.display =
            "none";

    }

}


window.closeAdminEditModal =
    closeAdminEditModal;


// ========================================
// SAVE ADMIN PROFILE
// ========================================

async function saveAdminProfile() {

    const loggedInUser =
        getLoggedInUser();


    if (
        !loggedInUser ||
        !loggedInUser.id
    ) {

        alert(
            "Admin login information not found."
        );

        return;

    }


    const name =
        getElement(
            "editAdminName"
        )
        ?.value
        .trim();


    const phoneNumber =
        getElement(
            "editAdminPhone"
        )
        ?.value
        .trim();


    const address =
        getElement(
            "editAdminAddress"
        )
        ?.value
        .trim();


    if (!name) {

        alert(
            "Name cannot be empty."
        );

        return;

    }


    // IMPORTANT:
    // Existing photo ko pehle preserve karo

    const existingPhoto =
        localStorage.getItem(
            ADMIN_PHOTO_KEY
        );


    try {

        const request = {

            id:
                loggedInUser.id,

            name:
                name,

            phoneNumber:
                phoneNumber,

            address:
                address

        };


        const response =
            await fetch(

                ADMIN_API_URL +
                "/update",

                {

                    method:
                        "PUT",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(
                            request
                        )

                }

            );


        if (!response.ok) {

            const errorMessage =
                await response.text();


            throw new Error(

                errorMessage ||
                "Update Failed"

            );

        }


        let updatedAdmin =
            null;


        const responseText =
            await response.text();


        if (responseText) {

            try {

                updatedAdmin =
                    JSON.parse(
                        responseText
                    );

            }

            catch (error) {

                console.log(
                    "Admin updated successfully."
                );

            }

        }


        if (!updatedAdmin) {

            updatedAdmin = {

                ...(window.currentAdmin || {}),

                id:
                    loggedInUser.id,

                name:
                    name,

                phoneNumber:
                    phoneNumber,

                address:
                    address,

                email:
                    window.currentAdmin?.email ||
                    loggedInUser.email

            };

        }


        window.currentAdmin = {

            ...(window.currentAdmin || {}),

            ...updatedAdmin,

            name:
                updatedAdmin.name ||
                name,

            phoneNumber:
                updatedAdmin.phoneNumber ??
                phoneNumber,

            address:
                updatedAdmin.address ??
                address

        };


        const newLoggedInUser = {

            ...loggedInUser,

            name:
                window.currentAdmin.name,

            email:
                window.currentAdmin.email ||
                loggedInUser.email,

            phoneNumber:
                window.currentAdmin.phoneNumber,

            address:
                window.currentAdmin.address

        };


        localStorage.setItem(

            "loggedInUser",

            JSON.stringify(
                newLoggedInUser
            )

        );


        updateAdminProfileUI(
            newLoggedInUser
        );


        // IMPORTANT:
        // Update ke baad existing photo wapas ensure karo

        if (existingPhoto) {

            localStorage.setItem(

                ADMIN_PHOTO_KEY,

                existingPhoto

            );


            restoreAdminPhoto();

        }


        closeAdminEditModal();


        alert(
            "Admin Profile Updated Successfully"
        );


        hideAllSections();


        const profileSection =
            getElement(
                "adminProfileSection"
            );


        if (profileSection) {

            profileSection.style.display =
                "block";

        }

    }


    catch (error) {

        console.error(

            "Admin Profile Update Error:",

            error

        );


        // Error ke baad bhi photo restore

        if (existingPhoto) {

            restoreAdminPhoto();

        }


        alert(

            error.message ||
            "Unable to update profile."

        );

    }

}


window.saveAdminProfile =
    saveAdminProfile;


// ========================================
// UPLOAD PHOTO BUTTON
// ========================================

function uploadAdminPhoto() {

    const input =
        getElement(
            "adminPhotoInput"
        );


    if (input) {

        input.click();

    }

}


window.uploadAdminPhoto =
    uploadAdminPhoto;


// ========================================
// COMPRESS PROFILE PHOTO
// ========================================

function compressAdminPhoto(
    file
) {

    return new Promise(

        function (
            resolve,
            reject
        ) {

            const reader =
                new FileReader();


            reader.onload =
                function (
                    event
                ) {

                    const image =
                        new Image();


                    image.onload =
                        function () {

                            const canvas =
                                document.createElement(
                                    "canvas"
                                );


                            let width =
                                image.width;


                            let height =
                                image.height;


                            const maxSize =
                                500;


                            if (
                                width > maxSize ||
                                height > maxSize
                            ) {

                                if (
                                    width >
                                    height
                                ) {

                                    height =
                                        Math.round(

                                            height *
                                            (
                                                maxSize /
                                                width
                                            )

                                        );


                                    width =
                                        maxSize;

                                }

                                else {

                                    width =
                                        Math.round(

                                            width *
                                            (
                                                maxSize /
                                                height
                                            )

                                        );


                                    height =
                                        maxSize;

                                }

                            }


                            canvas.width =
                                width;


                            canvas.height =
                                height;


                            const context =
                                canvas.getContext(
                                    "2d"
                                );


                            context.drawImage(

                                image,

                                0,

                                0,

                                width,

                                height

                            );


                            const compressedPhoto =
                                canvas.toDataURL(

                                    "image/jpeg",

                                    0.8

                                );


                            resolve(
                                compressedPhoto
                            );

                        };


                    image.onerror =
                        reject;


                    image.src =
                        event.target.result;

                };


            reader.onerror =
                reject;


            reader.readAsDataURL(
                file
            );

        }

    );

}


// ========================================
// INITIALIZE ADMIN PHOTO EVENT
// ========================================

function initializeAdminPhotoEvent() {

    const photoInput =
        getElement(
            "adminPhotoInput"
        );


    if (!photoInput) {

        return;

    }


    // Duplicate event listener avoid

    if (
        photoInput.dataset.listenerAttached ===
        "true"
    ) {

        return;

    }


    photoInput.dataset.listenerAttached =
        "true";


    photoInput.addEventListener(

        "change",

        async function (
            event
        ) {

            const file =
                event.target.files[0];


            if (!file) {

                return;

            }


            if (
                !file.type.startsWith(
                    "image/"
                )
            ) {

                alert(
                    "Please select an image file."
                );


                return;

            }


            try {

                // Compress image first

                const photoData =
                    await compressAdminPhoto(
                        file
                    );


                const photoElement =
                    getElement(
                        "adminProfilePhoto"
                    );


                if (photoElement) {

                    photoElement.src =
                        photoData;

                }


                // Save permanently in browser storage

                localStorage.setItem(

                    ADMIN_PHOTO_KEY,

                    photoData

                );


                // Clear input value
                // so same photo can be selected again

                event.target.value =
                    "";


                console.log(
                    "Admin profile photo saved successfully."
                );

            }


            catch (error) {

                console.error(
                    "Photo Upload Error:",
                    error
                );


                alert(
                    "Unable to upload photo."
                );

            }

        }

    );

}


// ========================================
// CHANGE ADMIN PASSWORD
// ========================================

async function changeAdminPassword() {

    const loggedInUser =
        getLoggedInUser();


    if (!loggedInUser) {

        alert(
            "Admin login information not found."
        );

        return;

    }


    const currentPassword =
        getElement(
            "currentAdminPassword"
        )?.value;


    const newPassword =
        getElement(
            "newAdminPassword"
        )?.value;


    const confirmPassword =
        getElement(
            "confirmAdminPassword"
        )?.value;


    if (
        !currentPassword ||
        !newPassword ||
        !confirmPassword
    ) {

        alert(
            "Please fill all fields."
        );

        return;

    }


    if (
        newPassword !==
        confirmPassword
    ) {

        alert(
            "New Password and Confirm Password do not match."
        );

        return;

    }


    try {

        const response =
            await fetch(

                ADMIN_API_URL +
                "/change-password",

                {

                    method:
                        "POST",

                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify({

                            userId:
                                loggedInUser.id,

                            currentPassword:
                                currentPassword,

                            newPassword:
                                newPassword

                        })

                }

            );


        const message =
            await response.text();


        if (response.ok) {

            alert(
                message
            );


            hideAdminChangePassword();

        }


        else {

            alert(
                message
            );

        }

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            "Unable to change password."
        );

    }

}


// ========================================
// SHOW ADMIN CHANGE PASSWORD
// ========================================

function showAdminChangePassword() {

    hideAllSections();


    const section =
        getElement(
            "adminChangePasswordSection"
        );


    if (section) {

        section.style.display =
            "block";

    }

}


window.showAdminChangePassword =
    showAdminChangePassword;


// ========================================
// HIDE ADMIN CHANGE PASSWORD
// ========================================

function hideAdminChangePassword() {

    showDashboard();

}


window.hideAdminChangePassword =
    hideAdminChangePassword;


// ========================================
// RECYCLE BIN
// ========================================

async function showRecycleBin() {

    hideAllSections();


    const section =
        getElement(
            "recycleBinSection"
        );


    if (section) {

        section.style.display =
            "block";

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/recycle-bin",

                {

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Unable to load recycle bin."
            );

        }


        const students =
            await response.json();


        const table =
            getElement(
                "recycleTable"
            );


        if (!table) {

            return;

        }


        table.innerHTML =
            "";


        if (
            !students ||
            students.length === 0
        ) {

            table.innerHTML = `

                <tr>

                    <td
                        colspan="5"
                        style="
                            text-align:center;
                            padding:30px;
                        "
                    >

                        No Deleted Students Found

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
                            ${student.id}
                        </td>

                        <td>
                            ${student.name ?? ""}
                        </td>

                        <td>
                            ${student.course ?? ""}
                        </td>

                        <td>
                            ${student.department ?? ""}
                        </td>

                        <td>

                            <button
                                class="edit-btn"
                                onclick="
                                    restoreStudent(
                                        ${student.id}
                                    )
                                "
                            >

                                ♻ Restore

                            </button>


                            <button
                                class="delete-btn"
                                onclick="
                                    permanentDeleteStudent(
                                        ${student.id}
                                    )
                                "
                            >

                                🗑 Delete Forever

                            </button>

                        </td>

                    </tr>

                `;

            }

        );

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            error.message
        );

    }

}


// ========================================
// HIDE RECYCLE BIN
// ========================================

function hideRecycleBin() {

    showDashboard();

}


window.hideRecycleBin =
    hideRecycleBin;


// ========================================
// RESTORE STUDENT
// ========================================

async function restoreStudent(
    id
) {

    try {

        const response =
            await fetch(

                API_URL +
                "/restore/" +
                id,

                {

                    method:
                        "PUT",

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Unable to restore student."
            );

        }


        alert(
            "Student Restored"
        );


        showRecycleBin();

        loadStudents();

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            error.message
        );

    }

}


// ========================================
// PERMANENT DELETE STUDENT
// ========================================

async function permanentDeleteStudent(
    id
) {

    if (

        !confirm(
            "Delete Forever?"
        )

    ) {

        return;

    }


    try {

        const response =
            await fetch(

                API_URL +
                "/permanent/" +
                id,

                {

                    method:
                        "DELETE",

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            throw new Error(
                "Permanent delete failed."
            );

        }


        alert(
            "Student Permanently Deleted"
        );


        showRecycleBin();

    }


    catch (error) {

        console.error(
            error
        );


        alert(
            error.message
        );

    }

}


window.showRecycleBin =
    showRecycleBin;


window.restoreStudent =
    restoreStudent;


window.permanentDeleteStudent =
    permanentDeleteStudent;


// ========================================
// AUDIT LOGS
// ========================================

async function showAuditLogs() {

    hideAllSections();


    const section =
        getElement(
            "auditLogsSection"
        );


    if (section) {

        section.style.display =
            "block";

    }


    await loadAuditLogs();

}


window.showAuditLogs =
    showAuditLogs;


// ========================================
// HIDE AUDIT LOGS
// ========================================

function hideAuditLogs() {

    showDashboard();

}


window.hideAuditLogs =
    hideAuditLogs;


// ========================================
// LOAD AUDIT LOGS
// ========================================

async function loadAuditLogs() {

    const table =
        getElement(
            "auditTable"
        );


    if (!table) {

        return;

    }


    table.innerHTML = `

        <tr>

            <td
                colspan="8"
                style="
                    text-align:center;
                    padding:30px;
                "
            >

                Loading Audit Logs...

            </td>

        </tr>

    `;


    try {

        const response =
            await fetch(

                AUDIT_API_URL,

                {

                    method:
                        "GET",

                    headers:
                        getAuthHeaders()

                }

            );


        if (!response.ok) {

            if (

                response.status === 401 ||

                response.status === 403

            ) {

                throw new Error(

                    "You are not authorized to view audit logs."

                );

            }


            throw new Error(

                "Unable to load audit logs."

            );

        }


        const data =
            await response.json();


        if (
            Array.isArray(
                data
            )
        ) {

            allAuditLogs =
                data;

        }


        else if (

            data &&

            Array.isArray(
                data.content
            )

        ) {

            allAuditLogs =
                data.content;

        }


        else {

            allAuditLogs =
                [];

        }


        displayAuditLogs(
            allAuditLogs
        );

    }


    catch (error) {

        console.error(

            "Audit Log Error:",

            error

        );


        table.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    style="
                        text-align:center;
                        padding:30px;
                        color:red;
                    "
                >

                    ${error.message}

                </td>

            </tr>

        `;

    }

}


// ========================================
// DISPLAY AUDIT LOGS
// ========================================

function displayAuditLogs(
    logs
) {

    const table =
        getElement(
            "auditTable"
        );


    if (!table) {

        return;

    }


    table.innerHTML =
        "";


    if (

        !logs ||

        logs.length === 0

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

                    No Audit Logs Found

                </td>

            </tr>

        `;


        return;

    }


    logs.forEach(

        log => {

            let createdAt =
                "-";


            if (log.createdAt) {

                const date =
                    new Date(
                        log.createdAt
                    );


                createdAt =
                    isNaN(
                        date.getTime()
                    )

                        ? log.createdAt

                        : date.toLocaleString();

            }


            const performedBy =
                log.userEmail ||
                "-";


            const role =
                log.role ||
                "-";


            const action =
                log.action ||
                "-";


            const module =
                log.module ||
                "-";


            const description =
                log.description ||
                "-";


            let target =
                "-";


            if (

                log.targetStudentId != null

            ) {

                target =
                    "Student #" +
                    log.targetStudentId;


                if (

                    log.targetStudentEmail

                ) {

                    target +=
                        "<br>" +

                        `<small>
                            ${log.targetStudentEmail}
                        </small>`;

                }

            }


            else if (

                log.targetAdminId != null

            ) {

                target =
                    "Admin #" +
                    log.targetAdminId;


                if (

                    log.targetAdminEmail

                ) {

                    target +=
                        "<br>" +

                        `<small>
                            ${log.targetAdminEmail}
                        </small>`;

                }

            }


            let actionClass =
                "audit-default";


            switch (

                action.toUpperCase()

            ) {

                case "LOGIN":

                    actionClass =
                        "audit-login";

                    break;


                case "LOGOUT":

                    actionClass =
                        "audit-logout";

                    break;


                case "CREATE":

                    actionClass =
                        "audit-create";

                    break;


                case "UPDATE":

                    actionClass =
                        "audit-update";

                    break;


                case "DELETE":

                    actionClass =
                        "audit-delete";

                    break;


                case "PASSWORD_CHANGE":

                    actionClass =
                        "audit-password";

                    break;

            }


            table.innerHTML += `

                <tr>

                    <td>
                        ${log.id ?? ""}
                    </td>

                    <td>
                        ${createdAt}
                    </td>

                    <td>
                        ${performedBy}
                    </td>

                    <td>
                        <strong>
                            ${role}
                        </strong>
                    </td>

                    <td>
                        <span
                            class="${actionClass}"
                        >
                            ${action}
                        </span>
                    </td>

                    <td>
                        ${module}
                    </td>

                    <td>
                        ${description}
                    </td>

                    <td>
                        ${target}
                    </td>

                </tr>

            `;

        }

    );

}


// ========================================
// SEARCH AUDIT LOGS
// ========================================

function searchAuditLogs() {

    const search =
        getElement(
            "auditSearch"
        )
        ?.value
        .trim()
        .toLowerCase() ||
        "";


    const action =
        getElement(
            "auditActionFilter"
        )
        ?.value
        .toUpperCase() ||
        "";


    const role =
        getElement(
            "auditRoleFilter"
        )
        ?.value
        .toUpperCase() ||
        "";


    const filtered =
        allAuditLogs.filter(

            log => {

                const email =
                    (
                        log.userEmail ||
                        ""
                    )
                    .toLowerCase();


                const logAction =
                    (
                        log.action ||
                        ""
                    )
                    .toUpperCase();


                const logRole =
                    (
                        log.role ||
                        ""
                    )
                    .toUpperCase();


                const description =
                    (
                        log.description ||
                        ""
                    )
                    .toLowerCase();


                const matchesSearch =

                    search === "" ||

                    email.includes(
                        search
                    ) ||

                    logAction
                        .toLowerCase()
                        .includes(
                            search
                        ) ||

                    description.includes(
                        search
                    );


                const matchesAction =

                    action === "" ||

                    logAction ===
                    action;


                const matchesRole =

                    role === "" ||

                    logRole ===
                    role;


                return (

                    matchesSearch &&

                    matchesAction &&

                    matchesRole

                );

            }

        );


    displayAuditLogs(
        filtered
    );

}


// ========================================
// AUDIT SEARCH EVENTS
// ========================================

document.addEventListener(

    "DOMContentLoaded",

    function () {

        const searchInput =
            getElement(
                "auditSearch"
            );


        const actionFilter =
            getElement(
                "auditActionFilter"
            );


        const roleFilter =
            getElement(
                "auditRoleFilter"
            );


        if (searchInput) {

            searchInput.addEventListener(

                "input",

                searchAuditLogs

            );

        }


        if (actionFilter) {

            actionFilter.addEventListener(

                "change",

                searchAuditLogs

            );

        }


        if (roleFilter) {

            roleFilter.addEventListener(

                "change",

                searchAuditLogs

            );

        }

    }

);


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


    // IMPORTANT:
    // Photo remove intentionally nahi kar rahe.
    //
    // Pehle code me:
    //
    // localStorage.removeItem("adminProfilePhoto");
    //
    // isi wajah se logout ke baad
    // photo permanently remove ho rahi thi.


    alert(
        "Logout Successful"
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


window.changeAdminPassword =
    changeAdminPassword;


window.searchAuditLogs =
    searchAuditLogs;