from flask import Blueprint, request, redirect, g, render_template, url_for

from utils import safe_cast

movie = Blueprint("movie", __name__)


@movie.route("/view", methods=["GET"])
def view():
    if not ("title" in request.args and "year" in request.args):
        return redirect("/")

    title = request.args["title"]
    year = safe_cast(request.args["year"], int)

    # Verify that the movie exists
    movie = g.movies.find_one({
        "title": title,
        "year": year
    })
    
    if not movie:
        return redirect("/")

    # Check if the user has a rating for this movie
    rating = g.movies.find_one({
        "_id": movie["_id"],
        "ratings": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    })

    # TODO: Check if this can be written better
    if rating:
        for i, x in enumerate(rating["ratings"]):
            if g.user.email == x["email"]:
                rating = rating["ratings"][i]

    # Check if the user has a comment for this movie
    comment = g.movies.find_one({
        "_id": movie["_id"],
        "comments": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    })

    # TODO: Check if this can be written better
    if comment:
        for i, x in enumerate(comment["comments"]):
            if g.user.email == x["email"]:
                comment = comment["comments"][i]

    return render_template("movie.html", movie=movie, rating=rating, comment=comment)


@movie.route("/rate", methods=["POST", "DELETE"])
def rate():
    return _comments_or_ratings("rating")


@movie.route("/comment", methods=["POST", "DELETE"])
def comment():
    return _comments_or_ratings("comment")


def _comments_or_ratings(field: str):
    """
    Since both the /rate and /comment routes have identical logic, this function does the majority of the work for both
    but changes the neccessary field required to have the correct functionality.

    Parameters
    ----------
    field : str
        The field that will be changed
    """
    if not ("title" in request.form and "year" in request.form):
        return "Missing 'title' and 'year' fields from request form", 400

    title = request.form["title"]
    year = safe_cast(request.form["year"], int)

    # Verify that the movie exists
    movie = g.movies.find_one({
        "title": title,
        "year": year
    })

    if not movie:
        return "Movie does not exist", 400

    # Get required field from movie (ratings or comments)
    match = g.movies.find_one({
        "_id": movie["_id"],
        f"{field}s": {
            "$elemMatch": {
                "email": g.user.email
            }
        }
    })

    if request.method == "POST":
        if match or not field in request.form:
            return f"{field} already exists or '{field}' field is missing from request form", 400

        data = request.form[field]

        # If field is "rating", cast it to a float since it is a decimal number (e.g. 9.8/10)
        if field == "rating":
            data = safe_cast(data, float)

        # Insert rating or comment to MongoDB
        g.movies.update_one({
            "_id": movie["_id"]
        }, {
            "$push": {
                f"{field}s": {
                    "email": g.user.email,
                    field: data
                }
            }
        })

        return redirect(url_for("movie.view", title=title, year=year))
    else:
        if not match:
            return f"{field} does not exist", 400

        # Delete rating or comment from MongoDB
        g.movies.update_one({
            "_id": movie["_id"]
        }, {
            "$pull": {
                f"{field}s": {
                    "email": g.user.email,
                }
            }
        })

        return "Done", 200


@movie.before_request
def before_request():
    if not g.user:
        return redirect("/")
