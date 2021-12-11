import os


def data_file(path):
    return os.path.join("data", path)

def cache_file(path):
    return os.path.join("cache", path)