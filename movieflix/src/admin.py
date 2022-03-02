from flask import Blueprint, render_template, request, g, redirect, url_for

from utils import safe_cast

admin = Blueprint("admin", __name__)


@admin.route("/", methods=["GET"])
def index():
    return render_template("admin.html")


@admin.route("/movies", methods=["GET", "DELETE"])
def movies():
    if request.method == "GET":
        return render_template("admin/movies.html", movies=g.movies.find())
    else:
        if not ("title" in request.form and "year" in request.form):
            return "Missing 'title' and 'year' keys from request form", 400

        # Verify that the movie exists
        movie = g.movies.find_one({
            "title": request.form["title"],
            "year": safe_cast(request.form["year"], int)
        })

        if movie:
            g.movies.delete_one(movie)
            return "Movie deleted", 200
        else:
            return "Movie does not exist", 404


@admin.route("/movies/new", methods=["GET", "POST"])
def movies_new():
    if request.method == "GET":
        return render_template("admin/movies/new.html", fail=("fail" in request.args))
    else:
        if not ("title" in request.form and "year" in request.form and "description" in request.form and "actors" in request.form):
            return redirect(url_for("admin.movies_new", fail=1))

        title = request.form["title"]
        actors = request.form["actors"].split(";")[:-1]
        
        if not title or len(actors) < 1:
            return redirect(url_for("admin.movies_new", fail=1))

        year = safe_cast(request.form["year"], int)

        # Verify that the movie doesn't already exist
        movie = g.movies.find_one({
            "title": title,
            "year": year
        })

        if movie:
            return redirect(url_for("admin.movies_new", fail=1))
        else:
            g.movies.insert_one({
                "title": title,
                "year": year,
                "description": request.form["description"],
                "actors": actors
            })

            return redirect("/admin/movies")


@admin.route("/movies/edit", methods=["GET", "PUT"])
def movies_edit():
    if request.method == "GET":
        if not ("title" in request.args and "year" in request.args):
            return "Missing 'title' and 'year' keys from request form", 400

        title = request.args["title"]
        year = safe_cast(request.args["year"], int)

        # Verify that the movie exists
        movie = g.movies.find_one({
            "title": title,
            "year": year
        })

        if movie:
            return render_template("admin/movies/edit.html", movie=movie)
        else:
            return "Movie does not exist", 404
    else:
        if not ("og-title" in request.form and "og-year" in request.form and "title" in request.form and "year" in request.form and "description" in request.form and "actors" in request.form):
            return "Missing 'og-title', 'og-year', 'title', 'year', 'description' and 'actors' keys from request form", 400

        # Verify that the movie exists
        movie = g.movies.find_one({
            "title": request.form["og-title"],
            "year": safe_cast(request.form["og-year"], int)
        })
        
        if movie:
            g.movies.update_one(movie, {
                "$set": {
                    "title": request.form["title"],
                    "year": safe_cast(request.form["year"], int),
                    "description": request.form["description"],
                    "actors": request.form["actors"].split(";")[:-1]
                }
            })

            return "Movie updated", 200
        else:
            return "Movie does not exist", 404


@admin.route("/users", methods=["GET", "PATCH", "DELETE"])
def users():
    if request.method == "GET":
        return render_template("admin/users.html", users=g.users.find())
    else:
        if not ("email" in request.form):
            return "Missing 'email' field from request form", 400

        email = request.form["email"]

        # Verify that user exists and is not an admin
        user = g.users.find_one({
            "email": email
        })

        if not user:
            return "User does not exist", 404
        if not user["category"] == "user":
            return "Cannot edit admin", 403

        if request.method == "PATCH":
            g.users.update_one(user, {
                "$set": {
                    "category": "admin"
                }
            })
        else:
            # Delete user ratings
            g.movies.update_many({
                "ratings": {
                    "$elemMatch": {
                        "email": email
                    }
                }
            }, {
                "$pull": {
                    "ratings": {
                        "email": email,
                    }
                }
            })
            
            # Delete user comments
            g.movies.update_many({
                "comments": {
                    "$elemMatch": {
                        "email": email
                    }
                }
            }, {
                "$pull": {
                    "comments": {
                        "email": email,
                    }
                }
            })

            # Delete user
            g.users.delete_one({
                "email": email
            })

        return "OK", 200


@admin.route("/movie/comment", methods=["DELETE"])
def movie_comment():
    if not ("email" in request.form and "title" in request.form and "year" in request.form):
        return "Missing 'email', 'title' and 'year' keys from request form", 400

    email = request.form["email"]
    title = request.form["title"]
    year = safe_cast(request.form["year"], int)

    g.movies.update_one({
        "title": title,
        "year": year
    }, {
        "$pull": {
            "comments": {
                "email": email,
            }
        }
    })

    return "OK", 200


@admin.before_request
def before_request():
    if not g.user or not g.user.category == "admin":
        return redirect("/")
