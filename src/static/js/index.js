const resultElement = document.getElementById("result");

const request = async function(form) {
    const result = await fetch("/predict", {
        method: "POST",
        body: new FormData(form)
    });

    return result.text();
}

const predict = function(form) {
    request(form).then(result => {
        console.log(result);
        resultElement.innerText = result;
    })
}
