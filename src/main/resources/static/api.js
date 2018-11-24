function updateOptions(element, data) {
    element.options.length = 1;
    for (item of data) {
        var opt = document.createElement("option");
        opt.value = item;
        opt.text = item;
        element.options.add(opt);
    }
}

function fetchTags() {
    console.log("fetchTags called...");
    var e = document.getElementById("consumer");
    var consumer = e.options[e.selectedIndex].value;
    var version = document.getElementById("version");
    fetch(`./api/tags/${consumer}`)
    .then(response => response.json())
    .then(data => updateOptions(version, data));
}

function updateConfiguration(configurations) {
    console.log("updateConfiguration called ...");
    var stack = document.getElementById("stack").value;
    var config = configurations.find(it => it.name === stack);
    if (config !== undefined) {
        console.log(config);
        document.getElementById("uri").value = config.path;
        document.getElementById("port").value = config.port;
        document.getElementById("assets").value = config.assetPath;
    }
}

function copyToClipboard() {
    /* Get the text field */
    var copyText = document.getElementById("stack-file");

    /* Select the text field */
    copyText.select();

    /* Copy the text inside the text field */
    document.execCommand("copy");

    var message = document.getElementById("message");
    message.innerHTML = "Stacken ble kopiert til utklippstavlen";

}