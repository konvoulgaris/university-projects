import nltk
import pandas as pd
import os

from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from bs4 import BeautifulSoup
from string import ascii_lowercase
from tqdm import tqdm

try:
    nltk.data.find("corpora/stopwords")
    nltk.data.find("tokenizers/punkt")
    nltk.data.find("corpora/wordnet")
except LookupError:
    nltk.download("wordnet")
    nltk.download("stopwords")
    nltk.download("punkt")

STOPWORDS = stopwords.words("english")
LEMMATIZER = WordNetLemmatizer()
TOKENIZER = nltk.data.load("tokenizers/punkt/english.pickle")

tqdm.pandas()


def clean_text(raw: str) -> str:
    """
    Cleans text to a simplified from more suited for machine learning models

    Parameters
    ----------
    raw : str
        The raw text that will be modified

    Returns
    -------
    str
        The resulting text
    """
    # Parse as HTML to avoid conflict with potential tags
    text = BeautifulSoup(raw, "html.parser").get_text()
    text = text.lower()
    
    # Keep only letters and spaces
    allowed = set(ascii_lowercase + " ")
    text = "".join(x for x in text if x in allowed)
    
    # Lemmatize and remove stopwords
    text = text.split()
    text = [LEMMATIZER.lemmatize(x) for x in text]
    text = [x for x in text if not x in STOPWORDS]
    
    return " ".join(text)


def text_to_sentences(raw: str) -> list():
    """
    Splits text to a list of sentences, where each sentence is a list of words

    Parameters
    ----------
    raw : str
        The text that will be modified

    Returns
    -------
    list()
        The resulting list of sentences
    """
    sentences = list()
    
    text = TOKENIZER.tokenize(raw)
    
    for x in text:
        if len(x) > 0:
            sentences.append(x.split())

    return sentences


def main(imdb_path: str, export_path: str):
    imdb = pd.read_csv(imdb_path, encoding="ISO-8859-1")

    # Export unlabelled reviews to test with later
    imdb_unknown = imdb[imdb["label"] == "unsup"]["review"]
    imdb_unknown.to_csv(os.path.join(export_path, "imdb_unknown.csv"), index=False)

    imdb = imdb[imdb["label"] != "unsup"]
    imdb["label"] = imdb["label"].map({ "neg": 0, "pos": 1 })
    imdb["review"] = imdb["review"].progress_apply(lambda x: clean_text(x))
    imdb.drop(["Unnamed: 0", "type", "file"], axis=1, inplace=True)
    imdb.to_csv(os.path.join(export_path, "imdb.csv"), index=False)


if __name__ == "__main__":
    main("data/imdb_master.csv", "data")
