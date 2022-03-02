# Base image
FROM continuumio/miniconda3

# Update system
RUN apt update -y
RUN apt upgrade -y

# Install Gunicorn
RUN conda install gunicorn

# Create project directory
WORKDIR /usr/src/app

# Install requirements
ADD spec-file.txt .
RUN conda install --file spec-file.txt

# Copy project files
ADD src .
ADD models models

# Expose ports
EXPOSE 5000

# Hack for NLTK downloads
RUN python -c "from text import *"

# Entrypoint
ENTRYPOINT gunicorn --bind :5000 --workers 3 app:app
