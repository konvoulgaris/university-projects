import numpy as np
import os

from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from joblib import dump
from tensorflow import keras


def train_kneighbors(data_path: str, export_path: str):
    """
    Train an KNeighbors model to identify the music genre of an audio file

    Parameters
    ----------
    data_path : str
        The path to the data
    export_path : str
        The path to export the model to
    """
    # Load and split data
    data = np.load(data_path)

    X = data["mfcc"]
    samples, x, y = X.shape
    X = X.reshape([samples, x * y])
    y = data["labels"]

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    # Generate and train model
    model = KNeighborsClassifier(n_neighbors=10, weights="distance")
    model.fit(X_train, y_train)
    
    print(model.score(X_test, y_test))
    
    # Export model
    dump(model, os.path.join(export_path, "knn.joblib"))


def train_svm(data_path: str, export_path: str):
    """
    Train an SVM model to identify the music genre of an audio file

    Parameters
    ----------
    data_path : str
        The path to the data
    export_path : str
        The path to export the model to
    """
    # Load and split data
    data = np.load(data_path)

    X = data["mfcc"]
    samples, x, y = X.shape
    X = X.reshape([samples, x * y])
    y = data["labels"]

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    # Generate and train model
    model = SVC(probability=True, verbose=1)
    model.fit(X_train, y_train)
    
    print(model.score(X_test, y_test))
    
    # Export model
    dump(model, os.path.join(export_path, "svm.joblib"))


def train_keras(data_path: str, export_path: str):
    """
    Trains a Keras model to identify the music genre of an audio file

    Parameters
    ----------
    data_path : str
        The path to the data
    export_path : str
        The path to export the model to
    """
    # Load and split data
    data = np.load(data_path)

    X = data["mfcc"]
    y = data["labels"]

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    # Create model
    model = keras.Sequential([
        keras.layers.Flatten(input_shape=(X.shape[1], X.shape[2])),

        keras.layers.Dense(512, activation='relu', kernel_regularizer=keras.regularizers.l2(0.001)),
        keras.layers.Dropout(0.3),

        keras.layers.Dense(256, activation='relu', kernel_regularizer=keras.regularizers.l2(0.001)),
        keras.layers.Dropout(0.3),

        keras.layers.Dense(64, activation='relu', kernel_regularizer=keras.regularizers.l2(0.001)),
        keras.layers.Dropout(0.3),

        keras.layers.Dense(10, activation='softmax')
    ])

    # Compile and train model
    model.compile(optimizer=keras.optimizers.Adam(learning_rate=0.0001), loss=keras.losses.SparseCategoricalCrossentropy(), metrics=['accuracy'])
    model.summary()
    model.fit(X_train, y_train, batch_size=32, validation_data=(X_test, y_test), epochs=75)

    # Export model
    model.save(os.path.join(export_path, "keras.h5"))


if __name__ == "__main__":
    train_kneighbors("data/data.npz", "models")
    train_svm("data/data.npz", "models")
    train_keras("data/data.npz", "models")
