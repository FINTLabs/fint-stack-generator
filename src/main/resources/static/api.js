function updateOptions(element, data, selected) {
    element.options.length = 1;
    for (item of data.sort()) {
        var opt = document.createElement("option");
        opt.value = item;
        opt.text = item;
        if (item === selected) {
            opt.selected = "selected";
        }
        element.options.add(opt);
    }
}

function refetch(consumer, version, provider) {
    console.log(`consumer = ${consumer} ${version}, provider = ${provider}`);
    fetchConsumers(consumer, version);
    fetchProviders(provider);
}

function fetchConsumers(actualConsumer, actualVersion) {
    console.log(`fetchConsumers(${actualConsumer},${actualVersion}) called...`);
    var consumer = document.getElementById("consumer");
    fetch(`./api/search/consumer`)
    .then(response => response.json())
    .then(data => updateOptions(consumer, data, actualConsumer));
    var version = document.getElementById("version");
    fetch(`./api/tags/${actualConsumer}`)
    .then(response => response.json())
    .then(data => updateOptions(version, data, actualVersion));
}

function fetchProviders(selected) {
    console.log(`fetchProviders(${selected}) called...`);
    var provider = document.getElementById("provider");
    fetch(`./api/tags/provider`)
    .then(response => response.json())
    .then(data => updateOptions(provider, data, selected));
}

function fetchTags() {
    console.log("fetchTags called...");
    var e = document.getElementById("consumer");
    var consumer = e.options[e.selectedIndex].value;
    var version = document.getElementById("version");
    fetch(`./api/tags/${consumer}`)
    .then(response => response.json())
    .then(data => updateOptions(version, data, ""));
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