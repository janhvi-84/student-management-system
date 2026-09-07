const API_URL =
"http://localhost:8081/auth/login";

document
.getElementById("loginForm")
.addEventListener(
"submit",
loginUser
);

async function loginUser(event) {


event.preventDefault();


const email =
    document
        .getElementById("email")
        .value
        .trim();


const password =
    document
        .getElementById("password")
        .value;


const selectedRole =
    document
        .getElementById("userType")
        .value;


if (

    email === "" ||

    password === "" ||

    selectedRole === ""

) {

    alert(
        "Please select Admin or Student and enter Email and Password."
    );

    return;

}


try {

    const response =
        await fetch(

            API_URL,

            {

                method:
                    "POST",

                headers: {

                    "Content-Type":
                        "application/json"

                },

                body:

                    JSON.stringify({

                        email:
                            email,

                        password:
                            password

                    })

            }

        );


    const responseText =
        await response.text();


    if (!response.ok) {

        alert(
            responseText
        );

        return;

    }


    const data =
        JSON.parse(
            responseText
        );


    if (!data.token) {

        alert(
            "Login token nahi mila."
        );

        return;

    }


    // CHECK SELECTED ROLE WITH DATABASE ROLE

    if (

        selectedRole !==
        data.role

    ) {

        alert(

            "Aapne galat login type select kiya hai. " +

            "Aapka account role hai: " +

            data.role

        );

        return;

    }


    localStorage.setItem(

        "token",

        data.token

    );


    localStorage.setItem(

        "loggedInUser",

        JSON.stringify({

            id:
                data.id,

            studentId:
                data.studentId,

            email:
                data.email,

            role:
                data.role,

            name:
                data.name

        })

    );


    // ADMIN DASHBOARD

    if (

        data.role ===
        "ADMIN"

    ) {

        window.location.href =
            "dashboard.html";

        return;

    }


    // STUDENT DASHBOARD

    if (

        data.role ===
        "STUDENT"

    ) {

        window.location.href =
            "student-dashboard.html";

        return;

    }


    alert(
        "Invalid user role."
    );


    localStorage.removeItem(
        "token"
    );


    localStorage.removeItem(
        "loggedInUser"
    );


}


catch (error) {

    console.error(

        "Login Error:",

        error

    );


    alert(

        "Server se connection nahi ho pa raha hai."

    );

}


}
