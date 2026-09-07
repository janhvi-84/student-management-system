// ========================================
// ADMIN MANAGEMENT
// ========================================

const API_URL = "http://localhost:8081/admin";


// ========================================
// PAGE LOAD
// ========================================

window.onload = function () {

    if (!checkAdminAccess()) {
        return;
    }

    loadAdmins();
    registerAdminForm();

};


// ========================================
// CHECK ADMIN ACCESS
// ========================================

function checkAdminAccess() {

    const token =
        localStorage.getItem("token");

        console.log("========== CREATE ADMIN ==========");
console.log("TOKEN =", token);
console.log("LOGGED USER =", localStorage.getItem("loggedInUser"));

    const loggedInUser =
        JSON.parse(
            localStorage.getItem("loggedInUser")
        );


    if (!token || !loggedInUser) {

        alert("Please login first");

        window.location.href =
            "login.html";

        return false;
    }


    if (loggedInUser.role !== "ADMIN") {

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
// REGISTER FORM
// ========================================

function registerAdminForm() {

    const form =
        document.getElementById("adminForm");


    if (!form) {
        return;
    }


    form.addEventListener(
        "submit",
        createAdmin
    );

}


// ========================================
// CREATE ADMIN
// ========================================

async function createAdmin(e) {

    e.preventDefault();


    const token =
        localStorage.getItem("token");


    const admin = {

        name:
            document
                .getElementById("name")
                .value
                .trim(),

        email:
            document
                .getElementById("email")
                .value
                .trim(),

        phoneNumber:
            document
                .getElementById("phoneNumber")
                .value
                .trim(),

        address:
            document
                .getElementById("address")
                .value
                .trim(),

        password:
            document
                .getElementById("password")
                .value

    };


    try {

        const response =
            await fetch(

                API_URL + "/create",

                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
                        JSON.stringify(admin)

                }

            );


        const responseText =
            await response.text();


        if (!response.ok) {

            alert(responseText);

            return;
        }


        alert(
            "Admin Created Successfully"
        );


        document
            .getElementById("adminForm")
            .reset();


        loadAdmins();


    } catch (error) {

        console.error(
            "Create Admin Error:",
            error
        );


        alert(
            "Server se connection nahi ho pa raha hai."
        );

    }

}


// ========================================
// LOAD ADMINS
// ========================================

async function loadAdmins() {

    const token =
        localStorage.getItem("token");


    try {

        const response =
            await fetch(

                API_URL,

                {

                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }

            );


        if (!response.ok) {

            throw new Error(
                "Unable to load admins"
            );

        }


        const admins =
            await response.json();


        displayAdmins(admins);


    } catch (error) {

        console.error(
            "Load Admin Error:",
            error
        );


        alert(
            "Admin data load nahi ho pa raha hai."
        );

    }

}


// ========================================
// DISPLAY ADMINS
// ========================================

function displayAdmins(admins) {

    const table =
        document.getElementById("adminTable");


    table.innerHTML = "";


    if (
        !admins ||
        admins.length === 0
    ) {

        table.innerHTML = `

            <tr>

                <td
                    colspan="6"
                    style="text-align:center">

                    No Admin Found

                </td>

            </tr>

        `;

        return;

    }


    admins.forEach(admin => {

        table.innerHTML += `

            <tr>

                <td>
                    ${admin.id ?? ""}
                </td>

                <td>
                    ${admin.name ?? ""}
                </td>

                <td>
                    ${admin.email ?? ""}
                </td>

                <td>
                    ${admin.phoneNumber ?? ""}
                </td>

                <td>

                    <span class="role-badge">

                        ${admin.role ?? "ADMIN"}

                    </span>

                </td>

                <td>

                    <button
                        class="delete-btn"
                        onclick="deleteAdmin(${admin.id})">

                        Delete

                    </button>

                </td>

            </tr>

        `;

    });

}


// ========================================
// DELETE ADMIN
// ========================================

async function deleteAdmin(id) {

    const confirmDelete =
        confirm(
            "Delete this admin?"
        );


    if (!confirmDelete) {
        return;
    }


    const token =
        localStorage.getItem("token");


    try {

        const response =
            await fetch(

                API_URL + "/" + id,

                {

                    method: "DELETE",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    }

                }

            );


        const responseText =
            await response.text();


        if (!response.ok) {

            alert(responseText);

            return;
        }


        alert(
            "Admin Deleted Successfully"
        );


        loadAdmins();


    } catch (error) {

        console.error(
            "Delete Admin Error:",
            error
        );


        alert(
            "Admin delete nahi ho pa raha hai."
        );

    }

}


// ========================================
// LOGOUT
// ========================================

function logout() {

    localStorage.removeItem("token");

    localStorage.removeItem(
        "loggedInUser"
    );

    window.location.href =
        "login.html";

}


// ========================================
// GLOBAL FUNCTIONS
// ========================================

window.deleteAdmin =
    deleteAdmin;

window.logout =
    logout;