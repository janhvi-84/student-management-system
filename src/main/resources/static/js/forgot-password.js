const API_URL = "http://localhost:8081/auth";

let verifiedEmail = "";

// ======================================
// SEND OTP
// ======================================

async function sendOtp() {

    const email =
        document.getElementById("email").value.trim();

    if (email === "") {

        alert("Please enter your email.");

        return;

    }

    try {

        const response =
            await fetch(

                API_URL + "/forgot-password",

                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body: JSON.stringify({

                        email: email

                    })

                }

            );

        const message =
            await response.text();

        alert(message);

        if (response.ok) {

            verifiedEmail = email;

        }

    }

    catch (error) {

        console.error(error);

        alert("Unable to send OTP.");

    }

}
// ======================================
// VERIFY OTP
// ======================================

async function verifyOtp() {

    const email =
        document.getElementById("email").value.trim();

    const otp =
        document.getElementById("otp").value.trim();

    if (email === "" || otp === "") {

        alert("Please enter Email and OTP.");

        return;

    }

    try {

        const response =
            await fetch(

                API_URL + "/verify-otp",

                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body: JSON.stringify({

                        email: email,
                        otp: otp

                    })

                }

            );

        const message =
            await response.text();

        alert(message);

        if (message === "OTP Verified") {

            verifiedEmail = email;

            document
                .getElementById("resetSection")
                .style.display = "block";

        }

    }

    catch (error) {

        console.error(error);

        alert("OTP verification failed.");

    }

}
// ======================================
// RESET PASSWORD
// ======================================

async function resetPassword() {

    const otp =
        document.getElementById("otp").value.trim();

    const newPassword =
        document.getElementById("newPassword").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    if (newPassword === "" || confirmPassword === "") {

        alert("Please enter new password.");

        return;

    }

    if (newPassword !== confirmPassword) {

        alert("Passwords do not match.");

        return;

    }

    try {

        const response =
            await fetch(

                API_URL + "/reset-password",

                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body: JSON.stringify({

                        email: verifiedEmail,
                        otp: otp,
                        newPassword: newPassword

                    })

                }

            );

        const message =
            await response.text();

        alert(message);

        if (message === "Password reset successful.") {

            window.location.href = "login.html";

        }

    }

    catch (error) {

        console.error(error);

        alert("Password reset failed.");

    }

}