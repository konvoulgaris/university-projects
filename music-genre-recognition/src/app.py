import os
import json

from flask import Flask, render_template, request, g
from joblib import load
from tensorflow import keras

from env import *
from api import api

# Create Flask app and register blueprints
app = Flask(__name__)
app.register_blueprint(api, url_prefix="/api")


@app.route("/", methods=["GET"])
def index():
    return render_template("index.html")


@app.route("/upload", methods=["GET"])
def upload():
    if not ("id" in request.args and "kneighbors" in request.args and "svm" in request.args and "keras" in request.args):
        return "Need 'id', 'kneighbors', 'svm' and 'keras' keys in request arguments", 400

    vid = request.args["id"]
    kneighbors = request.args["kneighbors"]
    svm = request.args["svm"]
    keras = request.args["keras"]

    with open(os.path.join(WORK_DIRECTORY, f"{vid}.json"), "r") as file:
        results = json.load(file)

    return render_template("upload.html", vid=vid, kneighbors=kneighbors, svm=svm, keras=keras, genres=g.genres, results=results)


@app.before_request
def before_request():
    if not "kneighbors" in g:
        g.kneighbors = load("models/kneighbors.joblib")
        g.svm = load("models/svm.joblib")
        g.keras = keras.models.load_model("models/keras.h5")

        with open("data/genres.json", "r") as file:
            g.genres = json.load(file)
            
        g.genres = dict((v, k) for k, v in g.genres.items())


if __name__ == "__main__":
    app.run(HOST, PORT, debug=True)
