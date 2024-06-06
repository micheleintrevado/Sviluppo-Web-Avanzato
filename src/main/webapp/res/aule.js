/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

let sendRestRequest = function (method, url, callback, acceptType = null, payload = null, payloadType = null, token = null, async = true) {
    let xhr = new XMLHttpRequest();
    xhr.open(method, url, async);
    if (token !== null)
        xhr.setRequestHeader("Authorization", "Bearer " + token);
    if (payloadType !== null)
        xhr.setRequestHeader("Content-Type", payloadType);
    if (acceptType !== null)
        xhr.setRequestHeader("Accept", acceptType);
    if (async) {
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                callback(xhr.responseText, xhr.status, xhr.getResponseHeader("Authorization"));
            }
        };
    }
    xhr.send(payload);
    if (!async) {
        callback(xhr.responseText, xhr.status, xhr.getResponseHeader("Authorization"));
    }
};

let handleAddAulaButton = function () {
    // nome: <input style="width:30%" type="text" value="C1.10" id="nome-aula" name="nome-field" />
    //                     luogo: <input style="width:30%" type="text" value="Coppito" id="luogo-aula" name="luogo-field" />
    //                     edificio: <input style="width:30%" type="text" value="Coppito" id="edificio-aula"
    //                         name="edificio-field" />
    //                     piano: <input style="width:30%" type="text" value="1" id="piano-aula" name="piano-field" />
    //                     capienza: <input style="width:30%" type="number" value="50" id="capienza-aula" name="capienza-field" />
    //                     email_responsabile: <input style="width:30%" type="text" value="u@u.u"
    //                         id="email_responsabile-aula" name="email_responsabile" />
    //                     prese_elettriche: <input style="width:30%" type="number" value="1" id="prese_elettriche_aula"
    //                         name="prese_elettriche-field" />
    //                     prese_rete: <input style="width:30%" type="number" value="1" id="prese_rete_aula"
    //                         name="prese_rete-field" />
    //                     note: <input style="width:30%" type="text" value="C1.10" id="note_aula" name="note" />
    //                     <input type="submit" value="addAula" name="addAula-button" id="addAula-button" />
    let n = document.getElementById("nome_aula");
    let l = document.getElementById("luogo_aula");
    let e = document.getElementById("edificio_aula");
    let p = document.getElementById("piano_aula");
    let c = document.getElementById("capienza_aula");
    let er = document.getElementById("email_responsabile_aula");
    let pe = document.getElementById("prese_elettriche_aula");
    let pr = document.getElementById("prese_rete_aula");
    let note = document.getElementById("note_aula");
    sendRestRequest(
        "post", "rest/aule",
        function (callResponse, callStatus, callAuthHeader) {
            if (callStatus === 200) {
                setToken(extractTokenFromHeader(callAuthHeader));
            } else {
                setToken(null);
            }
        },
        null,
        "nome=" + n.value + "&luogo=" + l.value + "&edificio=" + e.value + "&piano=" + p.value + "&capienza=" + c.value
        + "&email_responsabile=" + er.value + "&prese_elettriche=" + pe.value + "&prese_rete=" + pr.value
        + "&note=" + note.value, "application/x-json",
        null);
};
console.log("aule.js loaded");
let addAulaBtn = document.getElementById("addAula-button");
if (addAulaBtn)
    addAulaBtn.addEventListener("click", function (e) {
        handleAddAulaButton();
        e.preventDefault();
    });