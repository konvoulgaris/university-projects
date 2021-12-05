import pandas as pd
import os

from gensim.models import word2vec
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from joblib import dump

from word2vec import make_average_feature_vector


def main(imdb_path: str, word2vec_path: str, export_path: str):
    imdb = pd.read_csv(imdb_path)
    vector = word2vec.Word2Vec.load(word2vec_path)

    X_train, X_test, y_train, y_test = train_test_split(imdb["review"], imdb["label"], test_size=0.2)
    X_train = X_train.apply(lambda x: x.split())
    X_train = make_average_feature_vector(X_train, vector)
    X_test = X_test.apply(lambda x: x.split())
    X_test = make_average_feature_vector(X_test, vector)

    svm = SVC()
    svm.fit(X_train, y_train)
    print(svm.score(X_test, y_test))

    dump(svm, os.path.join(export_path, "svm.joblib"))


if __name__ == "__main__":
    main("data/imdb.csv", "models/word2vec.model", "models")
