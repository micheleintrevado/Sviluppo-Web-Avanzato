function getAula(index) {
    $.ajax({
        url: "rest/aule/" + index,
        method: "GET",
        success: function (data) {
            showAulaData(data);
        },
        error: function (request, status, error) {
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
            showAttrezzatureData(data);
        },
        error: function (request, status, error) {
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function getEvento(index) {
    $.ajax({
        url: "rest/eventi/" + index,
        method: "GET",
        success: function (data) {
            showEventoData(data);
        },
        error: function (request, status, error) {
            console.log("getAula error: " + request.status);
        },
        cache: false
    });
}

function showEventoData(data) {
    $('#get-evento-div #data_container').empty();
    let nomeElement = $('<p>').text('Eventi: ' + data.nome);
    let descrizioneElement = $('<p>').text('Descrizione: ' + data.descrizione);
    let orarioInizioElement = $('<p>').text('Orario Inizio: ' + formatDateUtility(data.orarioInizio));
    let orarioFineElement = $('<p>').text('Orario Fine: ' + formatDateUtility(data.orarioFine));
    let organizzatoreElement = $('<p>').text('Organizzatore: ' + data.nomeOrganizzatore);
    let responsabileElement = $('<p>').text('Responsabile: ' + data.emailResponsabile);
    let tipologiaElement = $('<p>').text('Tipologia: ' + data.tipologia);
    // let aulaElement = $('<p>').text('Aula: ' + getNomeAula(data.idAula));
    // let corsoElement = $('<p>').text('Corso: ' + data.idCorso);

    $('#get-evento-div #data_container').append(nomeElement).append(descrizioneElement)
        .append(orarioInizioElement).append(orarioFineElement).append(organizzatoreElement)
        .append(responsabileElement).append(tipologiaElement);
}

function showAttrezzatureData(data) {
    $('#get-attrezzature-div #data_container').empty();
    let attrezzatureTypes = data.map(attrezzatura => attrezzatura.tipo).join(', ');
    if (attrezzatureTypes.length == 0) {
        attrezzatureTypes = 'Nessuna attrezzatura associata';
    } else {
        attrezzatureTypes = 'Attrezzature associate: ' + attrezzatureTypes;
    }
    let attrezzatureElement = $('<p>').text(attrezzatureTypes);

    $('#get-attrezzature-div #data_container').append(attrezzatureElement);
}
function showAulaData(data) {
    $('#get-aule-div #data_container').empty();
    let nomeElement = $('<p>').text('Nome: ' + data.nome);
    let luogoElement = $('<p>').text('Luogo: ' + data.luogo);
    let edificioElement = $('<p>').text('Edificio: ' + data.edificio);
    let pianoElement = $('<p>').text('Piano: ' + data.piano);
    let capienzaElement = $('<p>').text('Capienza: ' + data.capienza);
    let emailResponsabileElement = $('<p>').text('Email Responsabile: ' + data.emailResponsabile);
    let preseElettricheElement = $('<p>').text('Prese Elettriche: ' + data.preseElettriche);
    let preseReteElement = $('<p>').text('Prese Rete: ' + data.preseRete);
    let noteElement = $('<p>').text('Note: ' + data.note);
    let attrezzatureTypes = data.attrezzatureAssociate.map(attrezzatura => attrezzatura.tipo).join(', ');
    if (attrezzatureTypes.length == 0) {
        attrezzatureTypes = 'Nessuna attrezzatura associata';
    }
    let attrezzatureElement = $('<p>').text('Attrezzature: ' + attrezzatureTypes);
    let gruppiElement = $('<p>').text('Gruppi: ' + data.gruppiAssociati.map(gruppo => gruppo.nome + " - " + gruppo.descrizione).join(', '));

    // let attrezzatureElement = $('<p>').text('Attrezzature: ' + data.attrezzatureAssociate[0].tipo + ' ' + data.attrezzatureAssociate[1].tipo);

    $('#data_container').append(nomeElement);
    $('#data_container').append(luogoElement);
    $('#data_container').append(edificioElement);
    $('#data_container').append(pianoElement);
    $('#data_container').append(capienzaElement);
    $('#data_container').append(emailResponsabileElement);
    $('#data_container').append(preseElettricheElement);
    $('#data_container').append(preseReteElement);
    $('#data_container').append(noteElement);
    $('#data_container').append(attrezzatureElement);
    $('#data_container').append(gruppiElement);
}

function addAula() {
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
            // header.substring("Bearer".length).trim();
            alert("addAula ok");
            console.log("addAula ok");
        },
        // success: function () {
        //     collezione_result.children().remove();
        //     clear();
        //     message("Collezione aggiornata con il nuovo disco.", "success");
        // },
        error: function (request, status, error) {
            console.log("addAula error: " + error);
        },
        // function (request, status, error) {
        //     handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
        // },
        cache: false
    });
}
;

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
            console.log("assignGruppoAula error: " + error);
        },
        cache: false
    });
};



function importAuleFromCSV() {
    //console.log("$('#fileCSV')[0].files[0]");
    //console.log($('#fileCSV')[0].files[0]);
    let formData = new FormData();
    formData.append('file', $('#fileCSV')[0].files[0]);
    //console.log("formData: ");
    //console.log(formData.get('file'));

    $.ajax({
        url: "rest/aule/csv",
        method: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (request, status, error) {
            alert("importAuleCSV ok");
            console.log("importAuleCSV ok");
        },
        error: function (request, status, error) {
            console.log("importAuleCSV error: " + error);
        },
        cache: false
    });
}

$('.scroll-link').on('click', function (event) {
    event.preventDefault();
    var target = $(this).attr('href');
    $('html, body').animate({
        scrollTop: $(target).offset().top
    }, 1000); // 1000 millisecondi = 1 secondo
});
getAuleUtility();
getGruppiUtility();
getCorsiUtility();