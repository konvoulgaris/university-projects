const DELETE = async function() {
    const response = await fetch("/account/delete", {
        method: "DELETE"
    })
}

const deleteAccount = function() {
    if (confirm("This action will permantely delete your account. Are you sure?")) {
        DELETE().then(response => {
            window.location.reload()
        })
    }
}
