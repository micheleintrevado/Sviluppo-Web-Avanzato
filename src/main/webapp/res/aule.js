function getAula(index) {
    $.ajax({
        url: "rest/aule/" + index,
        method: "GET",
        success: function (data) {
            showAulaData($('#get-aule-div'), data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function getAttrezzatura(index) {
    $.ajax({
        url: "rest/aule/" + index + "/attrezzature",
        method: "GET",
        success: function (data) {
            showAulaData($('#get-attrezzature-div'), data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function getEventiAulaSettimana(idAula, rangeStart) {
    $.ajax({
        url: "rest/aule/" + idAula + "/eventi",
        method: "GET",
        data: {
            rangeStart: rangeStart
        },
        success: function (data) {
            showEventoData($("#get-eventiInAula-div"), data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function showEventoData(mainContainer, data) {
    const events = Array.isArray(data) ? data : [data];
    const container = mainContainer.children('#data_container').empty();
    if (events.length === 0 || (events.length === 1 && !events[0].id)) {
        container.append('<p>Non ci sono eventi.</p>');
        return;
    }
    events.forEach(event => {
        const eventElement =
            `<div class="info-evento">
            <h2>${event.nome}</h2>
            <p>Inizio: ${formatDateUtility(event.orarioInizio)}</p>
            <p>Fine: ${formatDateUtility(event.orarioFine)}</p>
            <p>${event.descrizione}</p>
            <p>Organizzatore: ${event.nomeOrganizzatore}</p>
            <p>Email: ${event.emailResponsabile}</p>
            <p>Tipologia: ${event.tipologia}</p>
        </div>`;
        container.append(eventElement);
    });
}

function showAulaData(mainContainer, data) {
    mainContainer.children('#data_container').empty();
    const container = mainContainer.children('#data_container');
    let aulaElement = `<div class="info-aula">`;
    let attrezzatureElement, attrezzatureTypes;;
    if (data.nome) {
        aulaElement +=
            `<h2>${data.nome}</h2>
            <p>Piano: ${data.piano}</p>
            <p>Edificio: ${data.edificio}</p>
            <p>Capienza: ${data.capienza}</p>
            <p>Email Responsabile: ${data.emailResponsabile}</p>
            <p>Prese Elettriche: ${data.preseElettriche}</p>
            <p>Prese Rete: ${data.preseRete}</p>
            <p>Note: ${data.note}</p>`;
        attrezzatureTypes = data.attrezzatureAssociate.map(attrezzatura => attrezzatura.tipo).join(', ');
        if (attrezzatureTypes.length == 0) {
            attrezzatureTypes = 'Nessuna attrezzatura associata';
        }
        attrezzatureElement = `<p>Attrezzature: ${attrezzatureTypes} </p>`;
        let gruppiText = data.gruppiAssociati.map(gruppo => gruppo.nome + " - " + gruppo.descrizione).join(', ');
        let gruppiElement = `<p>Gruppi: ${gruppiText} </p>`;
        aulaElement = aulaElement + attrezzatureElement + gruppiElement + `</div>`;
    } else {
        let attrezzatureTypes = data.map(attrezzatura => attrezzatura.tipo).join(', ');
        if (attrezzatureTypes.length == 0) {
            attrezzatureTypes = 'Nessuna attrezzatura associata';
        } else {
            attrezzatureTypes = 'Attrezzature associate: ' + attrezzatureTypes;
        }
        attrezzatureElement = `<p>Attrezzature: ${attrezzatureTypes} </p>`;
        aulaElement = attrezzatureElement + `</div>`;
    }
    container.append(aulaElement);
}

function addAula() {
    let form = $('#crea_aula_form');

    // Check if the form is valid
    if (!checkRequired(form)) {
        console.log('Riempi tutti i campi');
        return;
    }
    //let token = document.getElementById("token-field").value;
    //message("", "");
    $.ajax({
        url: "rest/aule",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            nome: $('#nome_aula').val(),
            luogo: $('#luogo_aula').val(),
            edificio: $('#edificio_aula').val(),
            piano: $('#piano_aula').val(),
            capienza: parseInt($('#capienza_aula').val()),
            email_responsabile: $('#email_responsabile_aula').val(),
            prese_elettriche: parseInt($('#prese_elettriche_aula').val()),
            prese_rete: parseInt($('#prese_rete_aula').val()),
            note: $('#note_aula').val()
        }),
        success: function (request, status, error) {
            alert("addAula ok");
            getAuleUtility();
            $('#crea_aula_form').get(0).reset();
            console.log("addAula ok");
        },
        // success: function () {
        //     collezione_result.children().remove();
        //     clear();
        //     message("Collezione aggiornata con il nuovo disco.", "success");
        // },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("addAula error: " + error);
        },
        // function (request, status, error) {
        //     handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
        // },
        cache: false
    });
};

function assignGruppoAula(idAula) {
    $.ajax({
        url: "rest/aule/" + idAula + "/gruppi",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            id_gruppo: parseInt($('#id_gruppo_assign').val()),
        }),
        success: function (request, status, error) {
            alert("assignGruppoAula ok");
            console.log("assignGruppoAula ok");
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("assignGruppoAula error: " + error);
        },
        cache: false
    });
};


function importAuleFromCSV() {
    let formData = new FormData();
    formData.append('file', $('#fileCSV')[0].files[0]);
    $.ajax({
        url: "rest/aule/csv",
        method: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (request, status, error) {
            alert("importAuleCSV ok");
            getAuleUtility();
            console.log("importAuleCSV ok");
        },
        error: function (request, status, error) {
            handleError(request, status, error);
            console.log("importAuleCSV error: " + error);
        },
        cache: false
    });
}

$('#id_evento').on('input', function () {
    getEvento($(this).val());
});
$('#id_aula').on('input', function () {
    getAula($(this).val());
});
$('#id_aula_attrezzature').on('input', function () {
    getAttrezzatura($(this).val());
});
$('#range_start_settimana').on('input', function () {
    let startRange = new Date($(this).val());
    if (isNaN(startRange)) return;

    let endRange = new Date(startRange);
    endRange.setDate(startRange.getDate() + 7);

    $('#range_end_settimana').val(endRange.toISOString().slice(0, 16));

    $('#range_end_settimana_container').show();
});

getAuleUtility();
getGruppiUtility();
getCorsiUtility();