# Base image
FROM python:3.8-alpine

# Update system
RUN apk update
RUN apk upgrade --available
RUN pip install -U pip

# Install Gunicorn
RUN pip install gunicorn

# Create and use project directory
WORKDIR /usr/src/app

# Install requirements
ADD requirements.txt .
RUN pip install -r requirements.txt

# Copy project files
ADD src .
ADD data data

# Expose ports
EXPOSE 5000

# Entrypoint
ENTRYPOINT gunicorn --bind 5000 --workers 3 app:app
