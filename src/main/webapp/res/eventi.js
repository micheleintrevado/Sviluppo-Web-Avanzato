$(document).ready(function () {
    $('#tipologia_evento').change(function () {
        let selectedValue = $('#tipologia_evento').val();
        console.log(selectedValue);
        if (selectedValue === 'lezione' || selectedValue === 'esame' || selectedValue === 'parziale') {
            $('#corso_container').show();
            $('#id_corso_evento').attr('required', true);
            $('#id_corso_evento').empty();
            getCorsiUtility();
        } else {
            $('#corso_container').hide();
            $('#id_corso_evento').removeAttr('required');
            $('#id_corso_evento').empty();
            getCorsiUtility();
            $('#id_corso_evento').val(null);
        }
    });

    $('#tipologia_evento').trigger('change');

    $('#tipologia_evento_modifica').change(function () {
        let selectedValue = $('#tipologia_evento_modifica').val();
        if (selectedValue === 'lezione' || selectedValue === 'esame' || selectedValue === 'parziale') {
            $('#corso_container_modifica').show();
            $('#id_corso_evento_modifica').attr('required', true);
        } else {
            $('#corso_container_modifica').hide();
            $('#id_corso_evento_modifica').removeAttr('required');
            $('#id_corso_evento_modifica').empty();
            getCorsiUtility();
        }
    });

    $('#tipologia_evento_modifica').trigger('change');

    $('input[name="ricorrenza_evento"]').change(function () {
        let selectedValue = $('input[name="ricorrenza_evento"]:checked').val();

        if (selectedValue === 'giornaliera' || selectedValue === 'settimanale' || selectedValue === 'mensile') {
            $('#data_fine_ricorrenza').val($('#orario_fine').val());
            $('#ricorrenza_container').show();
        } else {
            $('#ricorrenza_container').css("display", "none");
        }
    });

    $('input[name="ricorrenza_evento"]:checked').trigger('change');
});

$('#reset_button').click(function () {
    $('#tipologia_evento').val('altro');
    $('#tipologia_evento').trigger('change');
});

$('#reset_button_modifica').click(function () {
    $('#tipologia_evento_modifica').val('altro');
    $('#tipologia_evento_modifica').trigger('change');
});

function getEvento(index) {
    if (index <= 0) {
        $('#get-evento-div').children('#data_container').empty();
        return;
    }
    $.ajax({
        url: "rest/eventi/" + index,
        method: "GET",
        success: function (data) {
            showEventoData($("#get-evento-div"), data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function getEventiAttuali(arrow, content) {
    $.ajax({
        url: 'rest/eventi/attuali',
        method: 'GET',
        success: function (data) {
            let info = '';
            if (data.length === 0) {
                info = '<p>Nessun evento attuale.</p>';
            }
            else {
                data.forEach(item => {
                    info += `<div><h3>${item.nome} - (${item.tipologia})</h3>`;
                    info += `<p>${item.descrizione}</p>`;
                    info += `<p>${formatDateUtility(item.orarioInizio)}  -  ${formatDateUtility(item.orarioFine)}</p>`;
                    info += `<p>${item.nomeOrganizzatore}, Contatti: ${item.emailResponsabile}</p></div>`;
                });
            }
            content.html(info);
            content.slideDown();
            arrow.addClass('down');
        },
        error: function (error) {
            content.html('<p>Errore nel recupero delle informazioni.</p>');
            content.slideDown();
            arrow.addClass('down');
        }
    });
}

function getEventiProssimi(arrow, content) {
    let prossimeOre = $('#prossime_ore').val();
    console.log(prossimeOre);
    $.ajax({
        url: 'rest/eventi/prossimi',
        method: 'GET',
        data: {
            prossimeOre: prossimeOre
        },
        success: function (data) {
            let info = '';
            if (data.length === 0) {
                info = '<p>Nessun evento prossimo.</p>';
            }
            else {
                console.log(data);
                data.forEach(item => {
                    info += `<div><h3>${item.nome} - (${item.tipologia})</h3>`;
                    info += `<p>${item.descrizione}</p>`;
                    info += `<p>${formatDateUtility(item.orarioInizio)}  -  ${formatDateUtility(item.orarioFine)}</p>`;
                    info += `<p>${item.nomeOrganizzatore}, Contatti: ${item.emailResponsabile}</p></div>`;
                });
            }
            content.html(info);
            content.slideDown();
            arrow.addClass('down');
        },
        error: function (request, status, error) {
            handleError(request, status, error);
        }
    });
}


function getEventiForRange(rangeStart, rangeEnd) {
    if (!rangeStart || !rangeEnd) {
        alert("Per favore, inserisci entrambi i valori di data e ora.");
        return;
    }

    if (new Date(rangeStart) >= new Date(rangeEnd)) {
        alert("La data di inizio deve essere precedente alla data di fine.");
        return;
    }
    $.ajax({
        url: "rest/eventi?rangeStart=" + rangeStart + "&rangeEnd=" + rangeEnd,
        method: "GET",
        success: function (data) {
            let blob = new Blob([data], { type: 'text/calendar' });
            let link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = 'calendar.ics';
            link.click();
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("get Eventi for range error: " + request.status);
        },
        cache: false,
        xhrFields: {
            responseType: 'blob'
        }
    })
}

function getEventiRicorrenti(idRicorrenza) {
    if (idRicorrenza <= 0) {
        return;
    }
    $.ajax({
        url: "rest/eventi/idMaster/" + idRicorrenza,
        method: "GET",
        success: function (data) {
            console.log(data);
            showEventoData($("#get-eventiRicorrenti-div"), data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("get Eventi for range error: " + request.status);
        },
        cache: false
    })
    console.log(idRicorrenza);
}

function addEvento() {
    let form = $('#crea_evento_form');
    if (!checkRequired(form)) {
        console.log('Riempi tutti i campi');
        return;
    }
    let tipo = $('input[name="ricorrenza_evento"]:checked').val();
    console.log(tipo);
    if (tipo !== "null") {
        $("#data_fine_ricorrenza").prop("hidden", false);
    }
    console.log($('#data_fine_ricorrenza').val());
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
            alert("Evento aggiunto correttamente");
            getEventiUtility();
            $('#crea_evento_form').get(0).reset();
            $('#tipologia_evento').trigger('change');
            $('input[name="ricorrenza_evento"]:checked').trigger('change');
            console.log("addEvento ok");
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("addEvento error: " + request.status);
        },
        cache: false
    });
}

function modificaEvento() {
    let form = $('#modifica_evento_form');
    if (!checkRequired(form)) {
        console.log('Riempi tutti i campi');
        return;
    }

    $.ajax({
        url: "rest/eventi/" + $('#id_evento_modifica').val(),
        method: "PATCH",
        contentType: "application/json",
        data: JSON.stringify({
            nome: $('#nome_evento_modifica').val(),
            orario_inizio: $('#orario_inizio_modifica').val(),
            orario_fine: $('#orario_fine_modifica').val(),
            descrizione: $('#descrizione_evento_modifica').val(),
            nome_organizzatore: $('#nome_organizzatore_evento_modifica').val(),
            email_responsabile: $('#email_responsabile_evento_modifica').val(),
            tipologia: $('#tipologia_evento_modifica').val(),
            id_aula: parseInt($('#id_aula_evento_modifica').val()),
            id_corso: parseInt($('#id_corso_evento_modifica').val())
        }),
        success: function (request, status, error) {
            alert("modificaEvento ok");
            getEventiUtility();
            $('#modifica_evento_form').get(0).reset();
            $('#tipologia_evento_modifica').trigger('change');
            console.log("modificaEvento ok");
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("modificaEvento error: " + request.status);
        },
        cache: false
    });
}


$(document).ready(function () {
    // I campi della form vengono modificati in base al valore dell'id evento selezionato
    $('#id_evento_modifica').change(function () {
        let idEvento = $(this).val();
        $('#id_corso_evento_modifica').empty();
        getCorsiUtility();
        $.ajax({
            url: 'rest/eventi/' + idEvento,
            type: 'GET',
            success: function (data) {
                $('#nome_evento_modifica').val(data.nome);
                $('#orario_inizio_modifica').val(formatDateTimeUtility(data.orarioInizio));
                $('#orario_fine_modifica').val(formatDateTimeUtility(data.orarioFine));
                $('#descrizione_evento_modifica').val(data.descrizione);
                $('#nome_organizzatore_evento_modifica').val(data.nomeOrganizzatore);
                $('#email_responsabile_evento_modifica').val(data.emailResponsabile);
                $('#tipologia_evento_modifica').val(data.tipologia).trigger('change');
                $('#id_aula_evento_modifica').val(data.idAula).trigger('change');
                $('#id_corso_evento_modifica').val(data.idCorso).trigger('change');
            },
            error: function (request, status, error) {
                $('#modifica_evento_form').trigger('reset');
            }
        });
    });
    //$('#tipologia_evento_modifica').trigger('change');

    $('#tipologia').change(function () {
        $('#id_corso_evento').val(null);
    }
    );
});

getRicorrenzeUtility();
getEventiUtility();
const addEventoBtn = $('#addEvento_button');
addEventoBtn.click(addEvento);
const modificaEventoBtn = $('#modificaEvento_button');
modificaEventoBtn.click(modificaEvento);