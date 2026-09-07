const API_URL = "http://localhost:8081/students";


// ======================================
// CONFIGURATION
// ======================================

const HOME_PAGE = "home.html";

window.currentStudent = null;


// ======================================
// PAGE LOAD
// ======================================

window.onload = function () {

    checkStudentAccess();

    initializePasswordEvents();

};


// ======================================
// CHECK LOGIN
// ======================================

function checkStudentAccess() {

    const token =
        localStorage.getItem("token");


    const loggedInUser =
        JSON.parse(
            localStorage.getItem("loggedInUser")
        );


    if (!token || !loggedInUser) {

        alert("Please login first.");

        window.location.href =
            "login.html";

        return;

    }


    if (loggedInUser.role !== "STUDENT") {

        alert("Access Denied");

        window.location.href =
            "dashboard.html";

        return;

    }


    loadStudentProfile(
        loggedInUser.id
    );

}


// ======================================
// AUTH HEADER
// ======================================

function getAuthHeaders() {

    const token =
        localStorage.getItem("token");


    return {

        "Content-Type":
            "application/json",

        "Authorization":
            "Bearer " + token

    };

}


// ======================================
// LOAD STUDENT PROFILE
// ======================================

async function loadStudentProfile(userId) {

    try {

        const response =
            await fetch(

                API_URL + "/user/" + userId,

                {

                    method:
                        "GET",

                    headers:
                        getAuthHeaders()

                }

            );


        if (

            response.status === 401 ||

            response.status === 403

        ) {

            logout();

            return;

        }


        if (!response.ok) {

            throw new Error(
                "Unable to load profile"
            );

        }


        const student =
            await response.json();


        window.currentStudent =
            student;


        showStudentData(
            student
        );


        updateProfileCompletion(
            student
        );


        updateActivityChart();

    }

    catch (error) {

        console.error(
            "Profile Load Error:",
            error
        );


        alert(
            "Profile load failed."
        );

    }

}


// ======================================
// SHOW STUDENT DATA
// ======================================

function showStudentData(student) {

    const name =
        student.name || "Student";


    const email =

        student.user &&
        student.user.email

            ?

            student.user.email

            :

            "Not Available";


    // ======================================
    // TOPBAR NAME
    // ======================================

    const studentName =
        document.getElementById(
            "studentName"
        );


    if (studentName) {

        studentName.innerText =
            name;

    }


    // ======================================
    // WELCOME NAME
    // ======================================

    const welcomeName =
        document.getElementById(
            "welcomeName"
        );


    if (welcomeName) {

        welcomeName.innerText =
            name;

    }


    // ======================================
    // DASHBOARD CARD NAME
    // ======================================

    const cardStudentName =
        document.getElementById(
            "cardStudentName"
        );


    if (cardStudentName) {

        cardStudentName.innerText =
            name;

    }


    // ======================================
    // PROFILE HEADER NAME
    // ======================================

    const profileName =
        document.getElementById(
            "profileName"
        );


    if (profileName) {

        profileName.innerText =
            name;

    }


    // ======================================
    // PROFILE DETAILS
    // ======================================

    const nameElement =
        document.getElementById(
            "name"
        );


    if (nameElement) {

        nameElement.innerText =
            student.name || "-";

    }


    const emailElement =
        document.getElementById(
            "email"
        );


    if (emailElement) {

        emailElement.innerText =
            email;

    }


    const phone =
        document.getElementById(
            "phone"
        );


    if (phone) {

        phone.innerText =
            student.phoneNumber || "-";

    }


    const course =
        document.getElementById(
            "course"
        );


    if (course) {

        course.innerText =
            student.course || "-";

    }


    const department =
        document.getElementById(
            "department"
        );


    if (department) {

        department.innerText =
            student.department || "-";

    }


    const city =
        document.getElementById(
            "city"
        );


    if (city) {

        city.innerText =
            student.city || "-";

    }


    const address =
        document.getElementById(
            "address"
        );


    if (address) {

        address.innerText =
            student.address || "-";

    }


    loadSavedStudentPhoto();

}


// ======================================
// LOAD SAVED PHOTO
// ======================================

function loadSavedStudentPhoto() {

    const loggedInUser =
        JSON.parse(

            localStorage.getItem(
                "loggedInUser"
            )

        );


    if (

        !loggedInUser ||

        !loggedInUser.id

    ) {

        return;

    }


    const photoKey =

        "studentProfilePhoto_" +

        loggedInUser.id;


    const savedPhoto =

        localStorage.getItem(
            photoKey
        );


    if (savedPhoto) {

        updateStudentPhoto(
            savedPhoto
        );

    }

    else {

        updatePhotoProgress(
            false
        );

    }

}


// ======================================
// UPDATE STUDENT PHOTO EVERYWHERE
// ======================================

function updateStudentPhoto(image) {

    const profilePhoto =
        document.getElementById(
            "profilePhoto"
        );


    const cardProfilePhoto =
        document.getElementById(
            "cardProfilePhoto"
        );


    const topProfilePhoto =
        document.getElementById(
            "topProfilePhoto"
        );


    if (profilePhoto) {

        profilePhoto.src =
            image;

    }


    if (cardProfilePhoto) {

        cardProfilePhoto.src =
            image;

    }


    if (topProfilePhoto) {

        topProfilePhoto.src =
            image;

    }


    updatePhotoProgress(
        true
    );


    if (window.currentStudent) {

        updateProfileCompletion(
            window.currentStudent
        );

    }

}


// ======================================
// UPDATE PHOTO PROGRESS
// ======================================

function updatePhotoProgress(hasPhoto) {

    const photoProgressIcon =
        document.getElementById(
            "photoProgressIcon"
        );


    if (!photoProgressIcon) {

        return;

    }


    if (hasPhoto) {

        photoProgressIcon.className =
            "progress-check";


        photoProgressIcon.innerText =
            "✓";

    }

    else {

        photoProgressIcon.className =
            "progress-pending";


        photoProgressIcon.innerText =
            "!";

    }

}


// ======================================
// PROFILE COMPLETION
// ======================================

function updateProfileCompletion(student) {

    if (!student) {

        return;

    }


    let totalFields =
        7;


    let completedFields =
        0;


    const fields = [

        student.name,

        student.phoneNumber,

        student.course,

        student.department,

        student.city,

        student.address

    ];


    fields.forEach(

        function (field) {

            if (

                field !== null &&

                field !== undefined &&

                String(field).trim() !== ""

            ) {

                completedFields++;

            }

        }

    );


    const loggedInUser =
        JSON.parse(

            localStorage.getItem(
                "loggedInUser"
            )

        );


    let hasPhoto =
        false;


    if (

        loggedInUser &&

        loggedInUser.id

    ) {

        const photoKey =

            "studentProfilePhoto_" +

            loggedInUser.id;


        hasPhoto =

            !!localStorage.getItem(
                photoKey
            );

    }


    if (hasPhoto) {

        completedFields++;

    }


    const percentage =

        Math.round(

            (

                completedFields /

                totalFields

            ) * 100

        );


    const profileCompletionPercent =
        document.getElementById(
            "profileCompletionPercent"
        );


    if (profileCompletionPercent) {

        profileCompletionPercent.innerText =
            percentage + "%";

    }


    const progressCircle =
        document.querySelector(
            ".progress-circle"
        );


    if (progressCircle) {

        const degree =

            (percentage / 100) *
            360;


        progressCircle.style.background =

            "conic-gradient(" +

            "#416ed1 0deg " +

            degree +

            "deg, " +

            "#e8edf5 " +

            degree +

            "deg 360deg)";

    }


    updatePhotoProgress(
        hasPhoto
    );

}


// ======================================
// ACTIVITY CHART
// ======================================

function updateActivityChart() {

    const chartBars =
        document.getElementById(
            "chartBars"
        );


    const activityPeriod =
        document.getElementById(
            "activityPeriod"
        );


    const engagementPercent =
        document.getElementById(
            "engagementPercent"
        );


    if (

        !chartBars ||

        !activityPeriod

    ) {

        return;

    }


    const period =
        activityPeriod.value;


    let labels;
    let values;
    let engagement;


    if (period === "month") {

        labels = [

            "Week 1",
            "Week 2",
            "Week 3",
            "Week 4"

        ];


        values = [

            62,
            75,
            84,
            91

        ];


        engagement =
            "84%";

    }

    else {

        labels = [

            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"

        ];


        values = [

            60,
            72,
            55,
            82,
            90,
            68,
            78

        ];


        engagement =
            "78%";

    }


    chartBars.innerHTML =
        "";


    values.forEach(

        function (

            value,
            index

        ) {

            const item =
                document.createElement(
                    "div"
                );


            item.className =
                "chart-bar-item";


            const bar =
                document.createElement(
                    "div"
                );


            bar.className =
                "chart-bar";


            if (

                value ===
                Math.max(
                    ...values
                )

            ) {

                bar.classList.add(
                    "highlight"
                );

            }


            bar.style.height =
                value + "%";


            const label =
                document.createElement(
                    "span"
                );


            label.innerText =
                labels[index];


            item.appendChild(
                bar
            );


            item.appendChild(
                label
            );


            chartBars.appendChild(
                item
            );

        }

    );


    if (engagementPercent) {

        engagementPercent.innerText =
            engagement;

    }

}


window.updateActivityChart =
    updateActivityChart;


// ======================================
// SHOW PROFILE
// ======================================

function showProfile() {

    const changePasswordSection =
        document.getElementById(
            "changePasswordSection"
        );


    const profileView =
        document.getElementById(
            "profileView"
        );


    if (changePasswordSection) {

        changePasswordSection.style.display =
            "none";

    }


    if (profileView) {

        profileView.classList.add(
            "show"
        );


        document.body.classList.add(
            "modal-open"
        );

    }

}


// ======================================
// HIDE PROFILE
// ======================================

function hideProfile() {

    const profileView =
        document.getElementById(
            "profileView"
        );


    if (profileView) {

        profileView.classList.remove(
            "show"
        );

    }


    document.body.classList.remove(
        "modal-open"
    );

}


// ======================================
// LOGOUT
// ======================================

function logout() {

    localStorage.removeItem(
        "token"
    );


    localStorage.removeItem(
        "loggedInUser"
    );


    window.location.href =
        "login.html";

}


window.logout =
    logout;


window.showProfile =
    showProfile;


window.hideProfile =
    hideProfile;


// ======================================
// EDIT PROFILE
// ======================================

function openEditModal() {

    if (!window.currentStudent) {

        alert(
            "Student data is loading. Please try again."
        );

        return;

    }


    const editModal =
        document.getElementById(
            "editModal"
        );


    if (editModal) {

        editModal.style.display =
            "flex";

    }


    document.body.classList.add(
        "modal-open"
    );


    document.getElementById(
        "editName"
    ).value =

        window.currentStudent.name || "";


    document.getElementById(
        "editAge"
    ).value =

        window.currentStudent.age || "";


    document.getElementById(
        "editCourse"
    ).value =

        window.currentStudent.course || "";


    document.getElementById(
        "editDepartment"
    ).value =

        window.currentStudent.department || "";


    document.getElementById(
        "editCity"
    ).value =

        window.currentStudent.city || "";


    document.getElementById(
        "editPhone"
    ).value =

        window.currentStudent.phoneNumber || "";


    document.getElementById(
        "editAddress"
    ).value =

        window.currentStudent.address || "";

}


// ======================================
// OPEN EDIT FROM PROFILE
// ======================================

function openEditModalFromProfile() {

    hideProfile();

    openEditModal();

}


// ======================================
// CLOSE EDIT MODAL
// ======================================

function closeEditModal() {

    const editModal =
        document.getElementById(
            "editModal"
        );


    if (editModal) {

        editModal.style.display =
            "none";

    }


    document.body.classList.remove(
        "modal-open"
    );

}


// ======================================
// SAVE PROFILE
// ======================================

async function saveProfile() {

    try {

        const loggedInUser =

            JSON.parse(

                localStorage.getItem(
                    "loggedInUser"
                )

            );


        if (

            !loggedInUser ||

            !loggedInUser.id

        ) {

            alert(
                "Please login again."
            );

            return;

        }


        const request = {

            userId:
                loggedInUser.id,

            name:

                document.getElementById(
                    "editName"
                ).value.trim(),


            age:

                parseInt(

                    document.getElementById(
                        "editAge"
                    ).value

                ) || null,


            course:

                document.getElementById(
                    "editCourse"
                ).value.trim(),


            department:

                document.getElementById(
                    "editDepartment"
                ).value.trim(),


            city:

                document.getElementById(
                    "editCity"
                ).value.trim(),


            phoneNumber:

                document.getElementById(
                    "editPhone"
                ).value.trim(),


            address:

                document.getElementById(
                    "editAddress"
                ).value.trim()

        };


        const response =
            await fetch(

                "http://localhost:8081/profile/update",

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


        if (

            response.status === 401 ||

            response.status === 403

        ) {

            logout();

            return;

        }


        if (!response.ok) {

            throw new Error(
                "Update failed"
            );

        }


        closeEditModal();


        await loadStudentProfile(
            loggedInUser.id
        );


        alert(
            "Profile Updated Successfully."
        );

    }

    catch (error) {

        console.error(
            "Profile Update Error:",
            error
        );


        alert(
            "Unable to update profile."
        );

    }

}


window.openEditModal =
    openEditModal;


window.openEditModalFromProfile =
    openEditModalFromProfile;


window.closeEditModal =
    closeEditModal;


window.saveProfile =
    saveProfile;


// ======================================
// GO DASHBOARD
// ======================================

function goDashboard() {

    hideProfile();


    const changePasswordSection =
        document.getElementById(
            "changePasswordSection"
        );


    if (changePasswordSection) {

        changePasswordSection.style.display =
            "none";

    }


    window.scrollTo({

        top:
            0,

        behavior:
            "smooth"

    });

}


window.goDashboard =
    goDashboard;


// ======================================
// GO HOME PAGE
// ======================================

function goHomePage() {

    window.location.href =
        HOME_PAGE;

}


window.goHomePage =
    goHomePage;


// ======================================
// SHOW CHANGE PASSWORD
// ======================================

function showChangePassword() {

    hideProfile();


    const changePasswordSection =
        document.getElementById(
            "changePasswordSection"
        );


    if (changePasswordSection) {

        changePasswordSection.style.display =
            "block";


        changePasswordSection.scrollIntoView({

            behavior:
                "smooth",

            block:
                "start"

        });

    }

}


window.showChangePassword =
    showChangePassword;


// ======================================
// HIDE CHANGE PASSWORD
// ======================================

function hideChangePassword() {

    const changePasswordSection =
        document.getElementById(
            "changePasswordSection"
        );


    if (changePasswordSection) {

        changePasswordSection.style.display =
            "none";

    }


    window.scrollTo({

        top:
            0,

        behavior:
            "smooth"

    });

}


window.hideChangePassword =
    hideChangePassword;


// ======================================
// UPLOAD PHOTO
// ======================================

function uploadPhoto() {

    const input =
        document.getElementById(
            "photoInput"
        );


    if (input) {

        input.click();

    }

    else {

        alert(
            "Photo upload input not found."
        );

    }

}


window.uploadPhoto =
    uploadPhoto;


// ======================================
// PHOTO UPLOAD EVENT
// ======================================

const photoInput =
    document.getElementById(
        "photoInput"
    );


if (photoInput) {

    photoInput.addEventListener(

        "change",

        function (e) {

            const file =
                e.target.files[0];


            if (!file) {

                return;

            }


            if (

                !file.type.startsWith(
                    "image/"
                )

            ) {

                alert(
                    "Please select a valid image file."
                );


                photoInput.value =
                    "";

                return;

            }


            const loggedInUser =

                JSON.parse(

                    localStorage.getItem(
                        "loggedInUser"
                    )

                );


            if (

                !loggedInUser ||

                !loggedInUser.id

            ) {

                alert(
                    "User information not found. Please login again."
                );

                return;

            }


            const reader =
                new FileReader();


            reader.onload =
                function (event) {

                    const image =
                        new Image();


                    image.onload =
                        function () {

                            const MAX_WIDTH =
                                500;


                            const MAX_HEIGHT =
                                500;


                            let width =
                                image.width;


                            let height =
                                image.height;


                            if (

                                width > height &&

                                width > MAX_WIDTH

                            ) {

                                height =
                                    Math.round(

                                        height *

                                        (
                                            MAX_WIDTH /
                                            width
                                        )

                                    );


                                width =
                                    MAX_WIDTH;

                            }


                            else if (

                                height > MAX_HEIGHT

                            ) {

                                width =
                                    Math.round(

                                        width *

                                        (
                                            MAX_HEIGHT /
                                            height
                                        )

                                    );


                                height =
                                    MAX_HEIGHT;

                            }


                            const canvas =
                                document.createElement(
                                    "canvas"
                                );


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


                            const compressedImage =

                                canvas.toDataURL(

                                    "image/jpeg",

                                    0.75

                                );


                            const photoKey =

                                "studentProfilePhoto_" +

                                loggedInUser.id;


                            try {

                                localStorage.setItem(

                                    photoKey,

                                    compressedImage

                                );


                                updateStudentPhoto(

                                    compressedImage

                                );


                                alert(
                                    "Photo Updated Successfully."
                                );

                            }

                            catch (error) {

                                console.error(

                                    "Photo Save Error:",

                                    error

                                );


                                alert(

                                    "Photo is too large to save. Please select a smaller image."

                                );

                            }


                            photoInput.value =
                                "";

                        };


                    image.src =
                        event.target.result;

                };


            reader.readAsDataURL(
                file
            );

        }

    );

}


// ======================================
// CHANGE PASSWORD
// ======================================

async function changePassword() {

    const currentPassword =

        document.getElementById(
            "currentPassword"
        ).value.trim();


    const newPassword =

        document.getElementById(
            "newPasswordChange"
        ).value.trim();


    const confirmPassword =

        document.getElementById(
            "confirmPasswordChange"
        ).value.trim();


    if (

        currentPassword === "" ||

        newPassword === "" ||

        confirmPassword === ""

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


    if (

        newPassword.length < 8 ||

        !/[A-Z]/.test(
            newPassword
        ) ||

        !/[a-z]/.test(
            newPassword
        ) ||

        !/[0-9]/.test(
            newPassword
        ) ||

        !/[^A-Za-z0-9]/.test(
            newPassword
        )

    ) {

        alert(
            "Please follow all password requirements."
        );

        return;

    }


    const loggedInUser =

        JSON.parse(

            localStorage.getItem(
                "loggedInUser"
            )

        );


    if (

        !loggedInUser ||

        !loggedInUser.id

    ) {

        alert(
            "Please login again."
        );

        return;

    }


    try {

        const response =
            await fetch(

                "http://localhost:8081/profile/change-password",

                {

                    method:
                        "PUT",

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


        alert(
            message
        );


        if (response.ok) {

            document.getElementById(
                "currentPassword"
            ).value =
                "";


            document.getElementById(
                "newPasswordChange"
            ).value =
                "";


            document.getElementById(
                "confirmPasswordChange"
            ).value =
                "";


            document.getElementById(
                "passwordStrength"
            ).innerHTML =
                "";


            document.getElementById(
                "passwordMatch"
            ).innerHTML =
                "";


            resetPasswordRules();


            hideChangePassword();

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


window.changePassword =
    changePassword;


// ======================================
// SHOW / HIDE PASSWORD
// ======================================

function togglePassword(
    inputId,
    icon
) {

    const input =
        document.getElementById(
            inputId
        );


    if (!input) {

        return;

    }


    if (

        input.type ===
        "password"

    ) {

        input.type =
            "text";


        icon.innerHTML =
            "🙈";

    }

    else {

        input.type =
            "password";


        icon.innerHTML =
            "👁";

    }

}


window.togglePassword =
    togglePassword;


// ======================================
// PASSWORD EVENTS
// ======================================

function initializePasswordEvents() {

    const passwordInput =

        document.getElementById(
            "newPasswordChange"
        );


    const confirmInput =

        document.getElementById(
            "confirmPasswordChange"
        );


    if (passwordInput) {

        passwordInput.addEventListener(

            "keyup",

            function () {

                validateNewPassword(
                    this.value
                );

            }

        );

    }


    if (confirmInput) {

        confirmInput.addEventListener(

            "keyup",

            function () {

                validateConfirmPassword();

            }

        );

    }

}


// ======================================
// VALIDATE NEW PASSWORD
// ======================================

function validateNewPassword(password) {

    checkRule(

        "ruleLength",

        password.length >= 8

    );


    checkRule(

        "ruleUpper",

        /[A-Z]/.test(
            password
        )

    );


    checkRule(

        "ruleLower",

        /[a-z]/.test(
            password
        )

    );


    checkRule(

        "ruleNumber",

        /[0-9]/.test(
            password
        )

    );


    checkRule(

        "ruleSpecial",

        /[^A-Za-z0-9]/.test(
            password
        )

    );


    let score =
        0;


    if (password.length >= 8) {

        score++;

    }


    if (/[A-Z]/.test(password)) {

        score++;

    }


    if (/[a-z]/.test(password)) {

        score++;

    }


    if (/[0-9]/.test(password)) {

        score++;

    }


    if (

        /[^A-Za-z0-9]/.test(
            password
        )

    ) {

        score++;

    }


    const strength =

        document.getElementById(
            "passwordStrength"
        );


    if (!strength) {

        return;

    }


    if (password === "") {

        strength.className =
            "password-strength";


        strength.innerHTML =
            "";

        return;

    }


    if (score <= 2) {

        strength.className =

            "password-strength weak";


        strength.innerHTML =

            "Weak Password";

    }


    else if (score <= 4) {

        strength.className =

            "password-strength medium";


        strength.innerHTML =

            "Medium Password";

    }


    else {

        strength.className =

            "password-strength strong";


        strength.innerHTML =

            "Strong Password";

    }


    validateConfirmPassword();

}


// ======================================
// VALIDATE CONFIRM PASSWORD
// ======================================

function validateConfirmPassword() {

    const confirmInput =

        document.getElementById(
            "confirmPasswordChange"
        );


    const passwordInput =

        document.getElementById(
            "newPasswordChange"
        );


    const match =

        document.getElementById(
            "passwordMatch"
        );


    if (

        !confirmInput ||

        !passwordInput ||

        !match

    ) {

        return;

    }


    if (

        confirmInput.value === ""

    ) {

        match.innerHTML =
            "";

        match.className =
            "password-match";

        return;

    }


    if (

        confirmInput.value ===
        passwordInput.value

    ) {

        match.className =

            "password-match strong";


        match.innerHTML =

            "✔ Passwords Match";

    }

    else {

        match.className =

            "password-match weak";


        match.innerHTML =

            "✖ Passwords Do Not Match";

    }

}


// ======================================
// PASSWORD RULE CHECK
// ======================================

function checkRule(
    id,
    valid
) {

    const rule =
        document.getElementById(
            id
        );


    if (!rule) {

        return;

    }


    const text =

        rule.innerText

            .replace(
                "✔ ",
                ""
            )

            .replace(
                "❌ ",
                ""
            );


    if (valid) {

        rule.className =
            "valid";


        rule.innerHTML =

            "✔ " +
            text;

    }

    else {

        rule.className =
            "invalid";


        rule.innerHTML =

            "❌ " +
            text;

    }

}


// ======================================
// RESET PASSWORD RULES
// ======================================

function resetPasswordRules() {

    const rules = [

        {
            id:
                "ruleLength",

            text:
                "Minimum 8 Characters"
        },

        {
            id:
                "ruleUpper",

            text:
                "One Uppercase Letter"
        },

        {
            id:
                "ruleLower",

            text:
                "One Lowercase Letter"
        },

        {
            id:
                "ruleNumber",

            text:
                "One Number"
        },

        {
            id:
                "ruleSpecial",

            text:
                "One Special Character"
        }

    ];


    rules.forEach(

        function (item) {

            const rule =
                document.getElementById(
                    item.id
                );


            if (rule) {

                rule.className =
                    "invalid";


                rule.innerHTML =
                    "❌ " +
                    item.text;

            }

        }

    );

}


// ======================================
// CLOSE MODALS ON OUTSIDE CLICK
// ======================================

window.addEventListener(

    "click",

    function (event) {

        const editModal =
            document.getElementById(
                "editModal"
            );


        if (

            editModal &&

            event.target ===
            editModal

        ) {

            closeEditModal();

        }

    }

);


// ======================================
// ESCAPE KEY
// ======================================

document.addEventListener(

    "keydown",

    function (event) {

        if (

            event.key ===
            "Escape"

        ) {

            closeEditModal();

            hideProfile();

        }

    }

);