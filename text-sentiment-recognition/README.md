# text-sentiment-recognition
A text sentiment recognition machine learning web app, originally developed for the requirements of the course [Advanced Topics in Data Analytics (DS-532)](https://www.ds.unipi.gr/en/courses/advanced-topics-in-data-analytics/).

It is designed to be able to accept text from the user, analyze it using a Word2Vec model and display the text sentiment prediction using an SVM model.

## How to Run
To make running this application easier, a Dockerfile is provided which automatically setups the application. Simply open your terminal, run the following commands:

```bash
docker build --rm -t text-sentiment-recognition:latest .
docker run --rm -d -p 5000:5000 text-sentiment-recognition:latest
```

And then go to the following address:

[http://localhost:5000](http://localhost:5000)

Enjoy! 😊

## How to Download Prerequisite Data
### Dataset
If you want to start completely from scratch, you will need the IMDb Review Dataset in a local directory named "data" and in a CSV file named "imdb_master.csv":

+ [IMDb Review Dataset Download](https://www.kaggle.com/utathya/imdb-review-dataset)

## License
This project is licensed under the [Apache Licence Version 2.0](LICENSE) by [Konstantinos Voulgaris](mailto:konstantinos@voulgaris.info)
