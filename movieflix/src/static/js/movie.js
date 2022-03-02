const updateRangeView = function(form) {
    let rangeView = document.getElementById("range-view")
    rangeView.innerText = form.rating.value
}

const DELETE = async function(form, endpoint, isAdmin=false) {
    const response = await fetch(`${isAdmin ? "/admin" : ""}/movie/${endpoint}`, {
        method: "DELETE",
        body: new FormData(form)
    })

    return response
}

const deleteRating = function(form) {
    DELETE(form, "rate").then(response => {
        window.location.reload()
    })
}

const deleteComment = function(form) {
    DELETE(form, "comment").then(response => {
        window.location.reload()
    })
}

const deleteCommentAdmin = function(form) {
    DELETE(form, "comment", true).then(response => {
        window.location.reload()
    })
}
