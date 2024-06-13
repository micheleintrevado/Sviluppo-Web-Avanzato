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
}
;



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

function getCorsiUtility() {
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
}
;

getAuleUtility();
getGruppiUtility();
getCorsiUtility();