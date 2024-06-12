$(document).ready(function () {
    $('#tipologia_evento').change(function () {
        let selectedValue = $('#tipologia_evento').val();
        if (selectedValue === 'lezione' || selectedValue === 'esame' || selectedValue === 'parziale') {
            $('#corso_container').show();
        } else {
            $('#corso_container').hide();
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
        console.log(selectedValue);
        if (selectedValue === 'giornaliera' || selectedValue === 'settimanale' || selectedValue === 'mensile') {
            $('#ricorrenza_container').show();
        } else {
            $('#ricorrenza_container').hide();
        }
    });

    $('input[name="ricorrenza_evento"]:checked').trigger('change');
});

$('#reset_button').click(function () {
    $('#tipologia_evento').val('altro');
    $('#tipologia_evento').trigger('change');
});

function addEvento() {
    const tipo = $('input[name="ricorrenza_evento"]:checked').val();
    if (tipo != null) {
        console.log(tipo);
        $("#data_fine_ricorrenza").prop("hidden", true);
    }
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
            tipo: $('input[name="ricorrenza_evento"]:checked').val(),
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
            if (request.status == 401) alert("LOGIN ERROR");
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
            id_aula: parseInt($('#id_aula_evento_modifica').val()),
            id_corso: parseInt($('#id_corso_evento_modifica').val())
        }),
        success: function (request, status, error) {
            // header.substring("Bearer".length).trim();
            alert("modificaEvento ok");
            console.log("modificaEvento ok");
        },
        error: function (request, status, error) {
            if (request.status == 401) alert("LOGIN ERROR");
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
        url: "rest/eventi/",
        method: "GET",
        success: function (data) {
            $.each(data, function (key) {
                $("[name='lista_id_eventi']").append(
                    "<option value=" + data[key] + ">" + data[key] + "</option>")
            });
        },
        error: function (request, status, error) {
            alert("ID EVENTI NON TROVATI");
            console.log("ID EVENTI NON TROVATI");
        },
        cache: false
    });
}

getEventiUtility();
const addEventoBtn = $('#addEvento_button');
addEventoBtn.click(addEvento);
const modificaEventoBtn = $('#modificaEvento_button');
modificaEventoBtn.click(modificaEvento);