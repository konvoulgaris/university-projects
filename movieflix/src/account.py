from flask import Blueprint, g, render_template, session, redirect

account = Blueprint("account", __name__)


@account.route("/", methods=["GET"])
def index():
    return render_template("account.html")


@account.route("/ratings", methods=["GET"])
def ratings():
    movies = g.movies.find({
        "ratings": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    })

    return render_template("account/ratings.html", movies=movies)


@account.route("/comments", methods=["GET"])
def comments():
    movies = g.movies.find({
        "comments": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    })

    return render_template("account/comments.html", movies=movies)


@account.route("/delete", methods=["DELETE"])
def delete():
    # Delete user ratings
    g.movies.update_many({
        "ratings": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    }, {
        "$pull": {
            "ratings": {
                "email": g.user.email,
            }
        }
    })
    
    # Delete user comments
    g.movies.update_many({
        "comments": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    }, {
        "$pull": {
            "comments": {
                "email": g.user.email,
            }
        }
    })

    # Delete user
    g.users.delete_one({
        "email": g.user.email
    })

    session.pop("email")
    return "OK", 200


@account.before_request
def before_request():
    if not g.user:
        return redirect("/")
