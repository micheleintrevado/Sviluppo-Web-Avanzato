/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

$(document).ready(function () {
    console.log("AUTH.JS");
    const login_btn = $("#login_button");
    login_btn.css("color","red");
    const logout_btn = $('#logout_button');
    let token = ''; // Variable to store the token

    // Send login request
    login_btn.click(function (event) {
        console.log("CLICK BOOM BOOM");
        event.preventDefault(); // Prevent default form submission

        const username = $('#username-field').val();
        const password = $('#password-field').val();
        $.ajax({
            url: 'rest/auth/login',
            type: 'POST',
            data: {
                username: username,
                password: password
            },
            success: function (data) {
                token = data.token; // Assuming the response contains the token in the 'token' field
                login_btn.css('color', 'red');
                $('#token-field').css('background-color', 'yellow');
                logout_btn.css('color', 'green');
                alert("Login effettuato con successo.");
            },
            error: function (request, status, error) {
                alert("Errore");
            },
            cache: false,
        });
    });

    // Send logout request
    logout_btn.click(function (event) {
        event.preventDefault(); // Prevent default form submission

        $.ajax({
            url: 'rest/auth/logout',
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token // Include the token in the logout request header
            },
            success: function () {
                token = ''; // Clear the token
                login_btn.css('color', 'green');
                $('#token-field').css('background-color', 'orange');
                logout_btn.css('color', 'red');
                alert("Logout effettuato con successo.");
            },
            error: function (request, status, error) {
                alert("Errore logout");
            },
            cache: false,
        });
    });
});