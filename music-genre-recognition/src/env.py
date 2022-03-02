import os

HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", 5000))
WORK_DIRECTORY = os.getenv("WORK_DIRECTORY", "/tmp")
