$(document).ready(function () {
    $('#tipologia_evento').change(function () {
        let selectedValue = $('#tipologia_evento').val();
        if (selectedValue === 'lezione' || selectedValue === 'esame' || selectedValue === 'parziale') {
            $('#corso_container').show();
            $('#id_corso_evento').empty();
            getCorsiUtility();
        } else {
            $('#corso_container').hide();
            $('#id_corso_evento').empty();
            getCorsiUtility();
            // $('#id_corso_evento').prepend('<option>Scegli un corso</option>');
            // $('#id_corso_evento').html('<option value="">Scegli un corso</option>');
            $('#id_corso_evento').val(null);
        }
    });

    $('#tipologia_evento').trigger('change');


    $('#tipologia_evento_modifica').change(function () {
        let selectedValue = $('#tipologia_evento_modifica').val();
        if (selectedValue === 'lezione' || selectedValue === 'esame' || selectedValue === 'parziale') {
            $('#corso_container_modifica').show();
        } else {
            $('#corso_container_modifica').hide();
        }
    });

    $('#tipologia_evento_modifica').trigger('change');

    $('input[name="ricorrenza_evento"]').change(function () {
        let selectedValue = $('input[name="ricorrenza_evento"]:checked').val();

        if (selectedValue === 'giornaliera' || selectedValue === 'settimanale' || selectedValue === 'mensile') {
            console.log(selectedValue);
            $('#data_fine_ricorrenza').val($('#orario_fine').val());
            $('#ricorrenza_container').show();
        } else {
            console.log("NIENTEEEEEE");
            $('#ricorrenza_container').css("display", "none");
            //$('#data_fine_ricorrenza').val();
        }

        //if (selectedValue !== "null") {
        //    $("#ricorrenza_container").prop("hidden", true);
        //}
    });

    $('input[name="ricorrenza_evento"]:checked').trigger('change');
});

$('#reset_button').click(function () {
    /*$('#data_fine_ricorrenza').val($('#orario_fine').val());
     $('input[name="ricorrenza_evento"]:checked').val(null);
     $('input[name="ricorrenza_evento"]:selected').trigger('change');*/

    $('#tipologia_evento').val('altro');
    $('#tipologia_evento').trigger('change');
});

function addEvento() {
    let tipo = $('input[name="ricorrenza_evento"]:checked').val();
    if (tipo !== "null") {
        $("#data_fine_ricorrenza").prop("hidden", true);
    }
    console.log($('#data_fine_ricorrenza').val())
    $.ajax({
        url: "rest/eventi",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            nome: $('#nome_evento').val(),
            orario_inizio: new Date($('#orario_inizio').val()).toISOString(),
            orario_fine: new Date($('#orario_fine').val()).toISOString(),
            descrizione: $('#descrizione_evento').val(),
            nome_organizzatore: $('#nome_organizzatore_evento').val(),
            email_responsabile: $('#email_responsabile_evento').val(),
            tipologia: $('#tipologia_evento').val(),
            id_aula: parseInt($('#id_aula_evento').val()),
            id_corso: parseInt($('#id_corso_evento').val()),
            tipo: $('input[name="ricorrenza_evento"]:checked').val() === "null" ? null : $('input[name="ricorrenza_evento"]:checked').val(),
            data_termine: new Date($('#data_fine_ricorrenza').val()).toISOString()
        }),
        success: function (request, status, error) {

            // header.substring("Bearer".length).trim();
            alert("addEvento ok");
            console.log("addEvento ok");
        },
        // success: function () {
        //     collezione_result.children().remove();
        //     clear();
        //     message("Collezione aggiornata con il nuovo disco.", "success");
        // },
        error: function (request, status, error) {
            if (request.status == 401)
                alert("LOGIN ERROR");
            console.log("addEvento error: " + request.status);
        },
        // function (request, status, error) {
        //     handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
        // },
        cache: false
    });
}

function modificaEvento() {
    $.ajax({
        url: "rest/eventi/" + $('#id_evento_modifica').val(),
        method: "PATCH",
        contentType: "application/json",
        data: JSON.stringify({
            nome: $('#nome_evento_modifica').val(),
            orario_inizio: new Date($('#orario_inizio_modifica').val()).toISOString(),
            orario_fine: new Date($('#orario_fine_modifica').val()).toISOString(),
            descrizione: $('#descrizione_evento_modifica').val(),
            nome_organizzatore: $('#nome_organizzatore_evento_modifica').val(),
            email_responsabile: $('#email_responsabile_evento_modifica').val(),
            tipologia: $('#tipologia_evento_modifica').val(),
            id_aula: $('#id_aula_evento_modifica').val(),
            id_corso: $('#id_corso_evento_modifica').val()
        }),
        success: function (request, status, error) {
            // header.substring("Bearer".length).trim();
            alert("modificaEvento ok");
            console.log("modificaEvento ok");
        },
        error: function (request, status, error) {
            if (request.status == 401)
                alert("LOGIN ERROR");
            console.log("modificaEvento error: " + request.status);
        },
        // function (request, status, error) {
        //     handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
        // },
        cache: false
    });
}

function getEventiUtility() {
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

$(document).ready(function () {
    // I campi della form vengono modificati in base al valore dell'id evento selezionato
    $('#id_evento_modifica').change(function () {
        let idEvento = $(this).val();

        $.ajax({
            url: 'rest/eventi/' + idEvento,
            type: 'GET',
            success: function (data) {
                console.log(data);
                $('#nome_evento_modifica').val(data.nome);
                $('#orario_inizio_modifica').val(formatDateTimeUtility(data.orarioInizio));
                $('#orario_fine_modifica').val(formatDateTimeUtility(data.orarioFine));
                $('#descrizione_evento_modifica').val(data.descrizione);
                $('#nome_organizzatore_evento_modifica').val(data.nomeOrganizzatore);
                $('#email_responsabile_evento_modifica').val(data.emailResponsabile);
                $('#tipologia_evento_modifica').val(data.tipologia);
                $('#id_aula_evento_modifica').val(data.idAula);
                $('#id_corso_evento_modifica').val(data.idCorso);
            },
            error: function (xhr, status, error) {
                $('#modifica_evento_form').trigger('reset');
            }
        });
    });

    // 
    $('#tipologia').change(function () {
        console.log("ho cambiato");
        $('#id_corso_evento').val(null);
    }
    );
});

function formatDateTimeUtility(dateArray) {
    const [year, month, day, hour, minute] = dateArray;
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
}
getEventiUtility();
const addEventoBtn = $('#addEvento_button');
addEventoBtn.click(addEvento);
const modificaEventoBtn = $('#modificaEvento_button');
modificaEventoBtn.click(modificaEvento);