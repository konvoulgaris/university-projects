const route = async function(endpoint, method, form) {
    const response = await fetch(`/admin/${endpoint}`, {
        method: method,
        body: new FormData(form)
    })

    return response
}

const promoteUser = function(form) {
    route("users", "PATCH", form).then(() => {
        window.location.reload()
    })
}

const deleteUser = function(form) {
    route("users", "DELETE", form).then(() => {
        window.location.reload()
    })
}

const goToNewMoviePage = function() {
    window.location.href = "/admin/movies/new"
}

const goToUpdateMoviePage = function(form) {
    window.location.href = `/admin/movies/edit?title=${form.title.value}&year=${form.year.value}`
}

const addActor = function(form) {
    let ul = document.getElementById("actors-list")
    let li = document.createElement("li")
    let value = form.actor.value
    
    if (value.length == 0)
        return
    
    form.actors.value = form.actors.value.concat(`${value};`)
    
    let text = document.createTextNode(value)
    li.appendChild(text)
    
    let span = document.createElement("span")
    span.classList = "uk-icon"
    span.setAttribute("uk-icon", "icon: close")
    span.onclick = removeActor
    span.style.cursor = "pointer"
    
    li.appendChild(span)
    li.classList = "uk-width-1-3 uk-flex uk-flex-around"
    ul.appendChild(li)
    
    form.actor.value = ""
}

const removeActor = function(span=null) {
    let li = (span.nodeName === "SPAN") ? span.parentElement : this.parentElement
    li.style.display = "none"
    
    let input = document.getElementById("actors-input")
    input.value = input.value.replace(`${li.innerText};`, '')
}

const editMovie = function(form) {
    route("movies/edit", "PUT", form).then(() => {
        window.location.href = "/admin/movies"
    })
}

const deleteMovie = function(form) {
    route("movies", "DELETE", form).then(() => {
        window.location.reload()
    })
}
