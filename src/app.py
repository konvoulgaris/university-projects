import os
import numpy as np

from gensim.models import word2vec
from joblib import load
from flask import Flask, render_template, request

from text import clean_text
from word2vec import make_average_feature_vector, make_feature_vector

HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", 5000))

vector = word2vec.Word2Vec.load("models/word2vec.model")
svm = load("models/svm.joblib")

app = Flask(__name__)


@app.route("/", methods=["GET"])
def index():
    return render_template("index.html")


@app.route("/predict", methods=["POST"])
def predict():
    if not "review" in request.form:
        return "Need 'review' key in request form", 400
    
    review = request.form["review"]
    
    X = make_feature_vector(clean_text(review).split(), vector)
    y = svm.predict(np.nan_to_num(X.reshape(1, -1)))[0]
    
    if y:
        return "Positive"
    else:
        return "Negative"


if __name__ == "__main__":
    app.run(HOST, PORT, True)
