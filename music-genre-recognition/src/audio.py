from threading import current_thread
import librosa
import math
import os
import numpy as np
import json

from tqdm import tqdm

SAMPLE_DURATION = 3


def extract_mfcc(audio_path: str) -> list():
    """
    Extracts MFCCs of 3-second windows from an audio file

    Parameters
    ----------
    audio_path : str
        The path to the audio file

    Returns
    -------
    list
        The resulting list with the extracted MFCCs
    """
    mfcc = list()
    
    signal, sr = librosa.load(audio_path)
    duration = math.floor(librosa.get_duration(signal))
    
    for i in range(0, duration - SAMPLE_DURATION, SAMPLE_DURATION):
        start = int(sr * i)
        end = int(start + (sr * SAMPLE_DURATION))
        mfcc.append(librosa.feature.mfcc(signal[start:end], n_mfcc=13).T)
    
    return mfcc


def main(gtzan_path: str, export_path: str):
    mfcc = list()
    labels = list()
    genres = dict()
    
    for i, (root, _, files) in enumerate(os.walk(gtzan_path)):
        if root != gtzan_path:
            current_genre = root.rsplit("/", 1)[-1]
            genres[current_genre] = i - 1
            
            for file in tqdm(files, desc=current_genre):
                extraced_mfcc = extract_mfcc(os.path.join(root, file))
                n_mfccs = len(extraced_mfcc)
                mfcc.extend(extraced_mfcc)
                labels.extend([genres[current_genre]] * n_mfccs)

    np.savez(os.path.join(export_path, "data.npz"),
             mfcc=np.array(mfcc), labels=np.array(labels))

    with open(os.path.join(export_path, "genres.json"), "w") as file:
        json.dump(genres, file)


if __name__ == "__main__":
    main("data/genres", "data/")
