# text-sentiment-recognition
A text sentiment recognition machine learning web app, originally developed for the requirements of the course [Advanced Topics in Data Analytics (DS-532)](https://www.ds.unipi.gr/en/courses/advanced-topics-in-data-analytics/).

It is designed to be able to accept text from the user, analyze it using a Word2Vec model and display the text sentiment prediction using an SVM model.

## How to Run
To make running this application easier, a Dockerfile is provided which automatically setups the application. Simply open your terminal, run the following command:

```bash
docker run --rm -d -p 5000:5000 konvoulgaris/text-sentiment-recognition:latest
```

And then go to the following address:

[http://localhost:5000](http://localhost:5000)

Alternatively, you can build the image yourself:

```bash
docker build --rm -t text-sentiment-recognition:latest .
docker run --rm -d -p 5000:5000 text-sentiment-recognition:latest
```

Enjoy! 😊

## How to Download Prerequisite Data
### Dataset
If you want to start completely from scratch, you will need the IMDb Review Dataset in a local directory named "data" and in a CSV file named "imdb_master.csv":

+ [IMDb Review Dataset Download](https://www.kaggle.com/utathya/imdb-review-dataset)
+ [IMDb Review Dataset Download (Fallback)](https://drive.google.com/file/d/1FqH1x4C9O8rJyD34826fv8kkHyDusXGR/view?usp=sharing)

### Training Data
If you want to train the models yourself, you will need the training data in in a local directory named "data" and in a CSV file named "imdb.csv":

+ [Training Data Download](https://drive.google.com/file/d/1C9EVWC7YIhHgAC_hQH0KHs2If_54hNgG/view?usp=sharing)

### Models
If you want to run the web application, you will need the Word2Vec model and the SVM model in a local directory named "models" and in two files named "word2vec.model" and "svm.joblib" respectively:

+ [Word2Vec Model Download](https://drive.google.com/file/d/1u3ZFxgc3C0kY6GkdfTPbXOnMqFtSNmTb/view?usp=sharing)
+ [SVM Model Download](https://drive.google.com/file/d/1e_0v9XA6lnUOVX7DhpXHFkU_RswTDXTS/view?usp=sharing)

## License
This project is licensed under the [Apache Licence Version 2.0](LICENSE) by [Konstantinos Voulgaris](mailto:konstantinos@voulgaris.info)
