$(document).ready(function () {
    const login_btn = $("#login-button");
    const logout_btn = $('#logout-button');
    const refresh_btn = $('#refresh-button');
    const token_field = $('#token-field');
    let token = '';

    // login
    login_btn.click(function (event) {
        event.preventDefault();
        // const form = $('#login-form');

        // if (form[0].checkValidity() === false) {
        //     alert("Riempi tutti i campi");
        //     return;
        // }

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
                login_btn.prop('disabled', true);
                logout_btn.prop('disabled', false);
                token_field.val(token);
                token_field.css('color', 'blue');
                alert("Login effettuato con successo.");
            },
            error: function (request, status, error) {
                handleError(request, status, error);
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
                login_btn.prop('disabled', false);
                logout_btn.prop('disabled', true);
                token_field.val(token);
                alert("Logout effettuato con successo.");
            },
            error: function (request, status, error) {
                handleError(request, status, error);
            },
            cache: false,
        });
    });

    refresh_btn.click(function (event) {
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
                alert("Refresh effettuato con successo.");
            },
            error: function (request, status, error) {
                handleError(request, status, error);
            },
            cache: false
        });

    });
});