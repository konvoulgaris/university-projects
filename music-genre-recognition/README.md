# music-genre-recognition
A music genre recognition machine learning web application, originally developed for the requirements of the course [Multimedia Communication (DS-406)](https://www.ds.unipi.gr/en/courses/multimedia-communications-2/).

It is designed to be able to accept YouTube URLs from the user, analyze it and display the music genre predictions from three machine learning models, which are as follows:
* KNeighbors
* SVM
* Keras Neural Network

## How to Run
To make running this application easier, a Dockerfile is provided which automatically setups the application. Simply open your terminal, run the following commands:

```bash
docker build --rm -t music-genre-recognition:latest .
docker run --rm -d -p 5000:5000 music-genre-recognition:latest
```

And then go to the following address:

[http://localhost:5000](http://localhost:5000)

Enjoy! 😊

## How to Download Prerequisite Data
### Dataset
If you want to start completely from scratch, you will need the GTZAN dataset in a local directory named "data" and a sub-directory named "genres"

+ [GTZAN Download](http://marsyas.info/downloads/datasets.html)

## Shout-out
I would like to shout-out Valerio velardo and his YouTube channel [Valerio Velardo - The Sound of AI](https://www.youtube.com/c/ValerioVelardoTheSoundofAI) because they helped me a ton when I was first developing this project.

## License
This project is licensed under the [Apache Licence Version 2.0](LICENSE) by [Konstantinos Voulgaris](mailto:konstantinos@voulgaris.info)
