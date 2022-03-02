import json
import pymongo.collection

def safe_cast(x, type, default = 0):
    """
    Safely casts a variable to another type. If the cast fails, the default value is returned.

    Parameters
    ----------
    x : [type]
        The variable that will be casted
    type : [type]
        The type the variable will be casted to
    default : [type], optional
        The default value that will be returned if the cast fails, by default None

    Returns
    -------
    [type]
        The resulting value of the operation
    """
    try:
        return type(x)
    except:
        return default


def insert_json(path: str, collection: pymongo.collection.Collection):
    """
    Inserts the data of a JSON file to a MongoDB collection

    Parameters
    ----------
    path : str
        The path of the JSON file
    collection : pymongo.collection.Collection
        The MongoDB collection
    """
    with open(path, "r") as f:
        data = json.load(f)
    
    for document in data:
        collection.update_one(document, { "$set": document }, upsert=True)
