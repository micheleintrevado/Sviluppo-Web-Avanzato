function formatDateUtility(data) {
    // formato della data: [anno, mese, giorno, ora, minuti]
    let date = new Date(data[0], data[1] - 1, data[2], data[3], data[4]);

    let optionsDate = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
    let optionsTime = { hour: '2-digit', minute: '2-digit' };

    let formattedDate = date.toLocaleDateString('it-IT', optionsDate);
    let formattedTime = date.toLocaleTimeString('it-IT', optionsTime);

    return `${formattedDate}, ${formattedTime}`;
}

function getCorsiUtility() {
    $("[name='lista_id_corso']").html("<option selected> Scegli un corso </option>");
    $.ajax({
        url: "rest/eventi/corsi/",
        method: "GET",
        success: function (data) {
            $("[name='lista_id_corso']").append("<option selected> Scegli un corso </option>");
            $.each(data, function (key) {
                $("[name='lista_id_corso']").append(
                    "<option value=" + data[key] + ">" + data[key] + "</option>");
            });
        },
        error: function (request, status, error) {
            alert("ID CORSI NON TROVATI");
            console.log("ID CORSI NON TROVATI");
        },
        cache: false
    });
}


function getAuleUtility() {
    $("[name='lista_id_aule']").html("<option selected> Scegli un aula </option>");
    $.ajax({
        url: "rest/aule/",
        method: "GET",
        success: function (data) {
            $.each(data, function (key) {
                $("[name='lista_id_aule']").append(
                    "<option value=" + data[key] + ">" + data[key] + "</option>");
            });
        },
        error: function (request, status, error) {
            alert("ID AULE NON TROVATI");
            console.log("ID AULE NON TROVATI");
        },
        cache: false
    });
}
function getGruppiUtility() {
    $("#id_gruppo_assign").html("<option selected> Scegli un gruppo </option>");
    $.ajax({
        url: "rest/aule/gruppi/",
        method: "GET",
        success: function (data) {
            $.each(data, function (key) {
                $("#id_gruppo_assign").append(
                    "<option value=" + data[key] + ">" + data[key] + "</option>"
                )
            });
        },
        error: function (request, status, error) {
            alert("ID GRUPPI AULE NON TROVATI");
            console.log("ID GRUPPI AULE NON TROVATI");
        },
        cache: false
    });
};


function getEventiUtility() {
    $("[name='lista_id_eventi']").html("<option selected> Scegli un evento </option>");
    $.ajax({
        url: "rest/eventi/ids",
        method: "GET",
        success: function (data) {
            $.each(data, function (key) {
                $("[name='lista_id_eventi']").append(
                    "<option value=" + data[key] + ">" + data[key] + "</option>");
            });
        },
        error: function (request, status, error) {
            alert("ID EVENTI NON TROVATI");
            console.log("ID EVENTI NON TROVATI");
        },
        cache: false
    });
}

function formatDateTimeUtility(dateArray) {
    const [year, month, day, hour, minute] = dateArray;
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
}

function handleError(request, status, error) {
    if (request.status === 404) {
        alert("Pagina non trovata");
    }
    if (request.status === 500) {
        alert("Errore interno del server");
    }
    if (request.status === 401) {
        alert("Login non effettuato");
    }
    if (request.status === 403) {
        alert("Utente non autorizzato");
    }
    if (request.status === 400) {
        alert("Errore di richiesta");
    }
    if (request.status === 0) {
        alert("Errore di connessione");
    }
    console.log("Error: " + error);
}
// $(document).ready(function () {
//     $('form').on('click', function (event) {
//         $(this).find('input[required]').each(function () {
//             if (!$(this).val()) {
//                 formValid = false;
//                 $(this).addClass('input-error');
//             } else {
//                 $(this).removeClass('input-error');
//             }
//         });
//     });
// });

$(document).ready(function () {
    $('div').on('click', function (event) {
        $('input[required]').removeClass('input-error');
        formValid = true;
        // Find all required inputs within the form
        $(this).find('input[required]').each(function () {
            if (!$(this).val()) {
                formValid = false;
                $(this).addClass('input-error'); // Add a class to highlight the error
            } else {
                // Remove the error message if it exists
                $(this).siblings('.error-message').remove();
                $(this).removeClass('input-error'); // Remove the error class if the field is filled
            }
        });
    });

    $('input[type="button"]').on('click', function (event) {
        if (!formValid) {
            // Remove any existing error messages
            $(this).siblings('.error-message').remove();
            // Add the error message
            $(this).parent().append('<span class="error-message">Riempi tutti i campi</span>');
            return false;
        } else {
            // Remove the error message if form is valid
            $(this).siblings('.error-message').remove();
        }
    });
});

function checkRequired(form) {
    let formValid = true;

    // Remove any existing error messages and input-error classes
    form.find('.error-message').remove();
    form.find('.input-error').removeClass('input-error');

    // Check each required input field
    form.find('input[required], textarea[required]').each(function () {
        if (!$(this).val()) {
            formValid = false;
            $(this).addClass('input-error');
            // Append error message if it doesn't already exist
            if ($(this).siblings('.error-message').length === 0) {
                $(this).after('<span class="error-message">Riempi tutti i campi</span>');
            }
        }
    });

    return formValid;
}

$('.scroll-link').on('click', function (event) {
    event.preventDefault();
    var target = $(this).attr('href');
    $('html, body').animate({
        scrollTop: $(target).offset().top
    }, 1000); // 1000 millisecondi = 1 secondo
});

$('.dropdown').on('click', function () {
    const arrow = $(this).find('.arrow');
    const content = $(this).find('.dropdown-content');
    const url = $(this).data('url');

    if (content.is(':visible') || $(this).find('#prossime_ore').is(':focus')) {
        console.log($(this).find('#prossime_ore').is(':focus'));
        content.slideUp();
        arrow.removeClass('down');
    } else {
        if (url.includes("attuali")) {
            getEventiAttuali(arrow, content);
        } else if (url.includes("prossimi")) {
            getEventiProssimi(arrow, content);
        }
    }
});