import os
import json
import numpy as np
import io

from youtube_dl import YoutubeDL
from flask import Blueprint, request, g, Response
from urllib import parse
from matplotlib.figure import Figure
from matplotlib.backends.backend_agg import FigureCanvasAgg

from env import *
from audio import extract_mfcc

YDL = YoutubeDL({
    "outtmpl": WORK_DIRECTORY + "/%(id)s.%(ext)s",
    "format": "bestaudio/best",
    "postprocessors": [
        {
            "key": "FFmpegExtractAudio",
            "preferredcodec": "mp3",
        }
    ],
})

api = Blueprint("api", __name__)


@api.route("/download", methods=["POST"])
def download():
    if not "url" in request.form:
        return "Need 'url' key in request form", 400

    url = request.form["url"]
    vid = parse.parse_qs(parse.urlparse(url).query)["v"][0]

    YDL.download([url])
    
    return { "id": vid, "path": os.path.join(WORK_DIRECTORY, f"{vid}.mp3")}, 200


@api.route("/process", methods=["POST"])
def result():
    data = json.loads(request.data)

    if not ("id" in data and "path" in data):
        return "Need 'id' and 'path' keys in request data", 400

    vid = data["id"]
    audio = np.array(extract_mfcc(data["path"]))

    # Predict with Keras
    y_keras = g.keras.predict(audio)
    segments = len(y_keras)
    r_keras = [0] * 10

    for y in y_keras:
        for i, genre in enumerate(y):
            r_keras[i] += genre
        
    r_keras = [x / segments for x in r_keras]

    # Reshape data
    samples, x, y = audio.shape
    audio = audio.reshape([samples, x * y])

    # Predict with KNeighbors
    y_kneighbors = g.kneighbors.predict_proba(audio)
    segments = len(y_kneighbors)
    r_kneighbors = [0] * 10
    
    for y in y_kneighbors:
        for i, genre in enumerate(y):
            r_kneighbors[i] += genre
        
    r_kneighbors = [x / segments for x in r_kneighbors]
    
    # Predict with SVM
    y_svm = g.svm.predict_proba(audio)
    segments = len(y_svm)
    r_svm = [0] * 10
    
    for y in y_svm:
        for i, genre in enumerate(y):
            r_svm[i] += genre
        
    r_svm = [x / segments for x in r_svm]

    prediction = { "kneighbors": r_kneighbors, "svm": r_svm, "keras": r_keras }

    with open(os.path.join(WORK_DIRECTORY, f"{vid}.json"), "w") as file:
        json.dump(prediction, file)
    
    return { "id": vid, "kneighbors": int(np.argmax(r_kneighbors)), "svm": int(np.argmax(r_svm)), "keras": int(np.argmax(r_keras)) }, 200


@api.route("/upload/<string:vid>.png", methods=["GET"])
def upload_png(vid):
    with open(os.path.join(WORK_DIRECTORY, f"{vid}.json"), "r") as file:
        results = json.load(file)
    
    # Plot results
    fig = Figure()
    ax = fig.add_subplot(1, 1, 1)
    ax.grid()
    ax.set_xlabel("Genres")
    ax.set_ylabel("Confidence")
    ax.plot(g.genres.keys(), results["kneighbors"], label="KNeighbors")
    ax.plot(g.genres.keys(), results["svm"], label="SVM")
    ax.plot(g.genres.keys(), results["keras"], label="Keras")
    ax.legend()

    # Generate PNG
    output = io.BytesIO()
    FigureCanvasAgg(fig).print_png(output)

    return Response(output.getvalue(), mimetype="image/png")
