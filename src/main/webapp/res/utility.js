function formatDateUtility(data) {
    // Assuming data is in the format [2024, 6, 15, 11, 0]
    let date = new Date(data[0], data[1] - 1, data[2], data[3], data[4]);

    // Formatting the date and time
    let optionsDate = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
    let optionsTime = { hour: '2-digit', minute: '2-digit' };

    let formattedDate = date.toLocaleDateString('it-IT', optionsDate);
    let formattedTime = date.toLocaleTimeString('it-IT', optionsTime);

    return `${formattedDate}, ${formattedTime}`;
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
};


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

function formatDateTimeUtility(dateArray) {
    const [year, month, day, hour, minute] = dateArray;
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
}