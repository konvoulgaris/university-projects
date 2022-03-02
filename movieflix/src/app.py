import os
import uuid
import re

from pymongo import MongoClient
from flask import Flask, render_template, request, redirect, g, session

from utils import safe_cast, insert_json
from authorization import authorization
from account import account
from admin import admin
from movie import movie
from models.User import User

# Get environment variables
HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", 5000))
MONGO_HOST = os.getenv("MONGO_HOST", "0.0.0.0")
MONGO_PORT = int(os.getenv("MONGO_PORT", 27017))

# Create and check the MongoDB connection
client = MongoClient(MONGO_HOST, MONGO_PORT)

try:
    client.server_info()
except:
    print("Failed to create a MongoDB connection!")
    exit(1)

db = client["MovieFlix"]

# Populate the "MovieFlix" database if empty
if not "Users" in db.list_collection_names():
    insert_json("data/users.json", db["Users"])
    insert_json("data/movies.json", db["Movies"])

users = db["Users"]
movies = db["Movies"]

# Create Flask application and register blueprints
app = Flask(__name__)
app.secret_key = uuid.uuid4().hex
app.register_blueprint(authorization, url_prefix="/")
app.register_blueprint(account, url_prefix="/account")
app.register_blueprint(admin, url_prefix="/admin")
app.register_blueprint(movie, url_prefix="/movie")


@app.route("/", methods=["GET"])
def index():
    if g.user:
        return render_template("index.html", movies=movies.find(), users=users.find())
    else:
        return redirect("/login")


@app.route("/search", methods=["POST"])
def search():
    search = request.form["search"]
    regex = re.compile(f"(?i).*{search}.*") # Containing the "search" pattern
    
    # Find any movie that matches any of the following criteria
    match = g.movies.find({
        "$or": [
            {
                "title": regex
            },
            {
                "year": safe_cast(search, int)
            },
            {
                "actors": {
                    "$in": [
                        regex
                    ]
                }
            }
        ]
    })

    return render_template("components/results.html", movies=match)


@app.before_request
def before_request():
    # Populate g if empty
    if not "movies" in g:
        g.users = users
        g.movies = movies
        g.user = None

    # Verify user session
    if "email" in session and not g.user:
        match = g.users.find_one({
            "email": session["email"]
        })
        g.user = User.make_from_document(match)
    else:
        g.user = None


if __name__ == "__main__":
    app.run(HOST, PORT, True)
