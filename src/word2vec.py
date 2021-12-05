import pandas as pd
import numpy as np
import os

from tqdm import tqdm
from gensim.models import word2vec

from text import text_to_sentences

FEATURE_COUNT = 300
MIN_WORD_COUNT = 40
WORKER_COUNT = 4
WINDOW_SIZE = 10
DOWNSAMPLING = 0.0001


def make_feature_vector(words: list(), vector: word2vec.Word2Vec) -> np.array:
    """
    Averages the vectors of a list of words

    Parameters
    ----------
    words : list
        The list of words
    vector : word2vec.Word2Vec
        The Word2Vec model

    Returns
    -------
    np.array
        The resulting NumPy array
    """
    feature = np.zeros((FEATURE_COUNT, ))

    indices = set(vector.wv.index2word)
    num_words = 0

    for word in words:
        if word in indices:
            feature = np.add(feature, vector[word])
            num_words += 1

    feature = np.divide(feature, num_words)

    return feature


def make_average_feature_vector(reviews: list(), vector: word2vec.Word2Vec) -> np.array:
    """
    Averages the vectors of a list of list of words

    Parameters
    ----------
    reviews : list
        The list of list of words
    vector : word2vec.Word2Vec
        The Word2Vec model

    Returns
    -------
    np.array
        The resulting NumPy array
    """
    average_feature = np.zeros((len(reviews), FEATURE_COUNT))

    counter = 0

    for review in tqdm(reviews, desc="Average Feature Vector"):
        average_feature[counter] = make_feature_vector(review, vector)
        counter += 1

    return average_feature


def main(imdb_path: str, export_path: str):
    imdb = pd.read_csv(imdb_path)
    
    sentences = list()

    for review in tqdm(imdb["review"], desc="Text to Sentences"):
        sentences.extend(text_to_sentences(review))

    vector = word2vec.Word2Vec(sentences, size=FEATURE_COUNT,
                               min_count=MIN_WORD_COUNT, workers=WORKER_COUNT,
                               window=WINDOW_SIZE, sample=DOWNSAMPLING)
    vector.init_sims(replace=True)
    vector.save(os.path.join(export_path, "word2vec.model"))


if __name__ == "__main__":
    main("data/imdb.csv", "models")
