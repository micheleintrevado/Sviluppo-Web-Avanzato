$(document).ready(function () {
    const login_btn = $("#login-button");
    const logout_btn = $('#logout-button');
    const refresh_btn = $('#refresh-button');
    const token_field = $('#token-field');
    let token = '';

    // Send login request
    login_btn.click(function (event) {
        event.preventDefault();

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
                token = data; 
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
                token = data; 
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
            cache: false
        });
                
    });
});