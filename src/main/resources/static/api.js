function updateOptions(element, data) {
    element.options.length = 1;
    for (item of data) {
        var opt = document.createElement("option");
        opt.value = item;
        opt.text = item;
        element.options.add(opt);
    }
}

function refetch() {
    fetchConsumers();
    fetchProviders();
}

function fetchConsumers() {
    console.log("fetchConsumers called...");
    var e = document.getElementById("repository");
    var repo = e.options[e.selectedIndex].value;
    var consumer = document.getElementById("consumer");
    fetch(`./api/search/${repo}/consumer`)
    .then(response => response.json())
    .then(data => {
        updateOptions(consumer, data);
        fetchTags();
    });
}

function fetchProviders() {
    console.log("fetchProviders called...");
    var e = document.getElementById("repository");
    var repo = e.options[e.selectedIndex].value;
    var provider = document.getElementById("provider");
    fetch(`./api/tags/${repo}/provider`)
    .then(response => response.json())
    .then(data => updateOptions(provider, data));
}

function fetchTags() {
    console.log("fetchTags called...");
    var e = document.getElementById("repository");
    var repo = e.options[e.selectedIndex].value;
    e = document.getElementById("consumer");
    var consumer = e.options[e.selectedIndex].value;
    var version = document.getElementById("version");
    fetch(`./api/tags/${repo}/${consumer}`)
    .then(response => response.json())
    .then(data => updateOptions(version, data));
}
