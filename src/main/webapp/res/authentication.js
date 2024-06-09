/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

$(document).ready(function () {
    console.log("AUTH.JS");
    const login_btn = $("#login-button");
    const logout_btn = $('#logout-button');
    const refresh_btn = $('#refresh-button');
    const token_field = $('#token-field');
    let token = ''; // Variable to store the token

    // Send login request
    login_btn.click(function (event) {
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
                token = data; // Assuming the response contains the token in the 'token' field
                $.ajaxSetup({
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });
                token_field.val(token);
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
        event.preventDefault();

        $.ajax({
            url: 'rest/auth/logout',
            type: 'DELETE',
            success: function () {
                token = ''; // Clear the token
                $.ajaxSetup({
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });
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
    
    refresh_btn.click(function(event){
        event.preventDefault();
        
        $.ajax({
            url: 'rest/auth/refresh',
            type: 'GET',
            success: function (data) {
                token = data; // Assuming the response contains the token in the 'token' field
                $.ajaxSetup({
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });
                token_field.val(token);
                refresh_btn.css('color', 'blue');
                $('#token-field').css('background-color', 'green');
                alert("Refresh effettuato con successo.");
            },
            error: function (request, status, error) {
                alert("Errore Refresh");
            },
            cache: false,
        });
                
    });
});