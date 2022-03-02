const search = async function(form) {
    const response = await fetch("/search", {
        method: "POST",
        body: new FormData(form)
    })

    return response.text()
}

const searchMovies = function(form) {
    const view = document.getElementById("view")

    search(form).then(response => {
        view.innerHTML = response
    })
}
