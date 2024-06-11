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

const addEventoBtn = $('#addEvento_button');
addEventoBtn.click(addEvento);