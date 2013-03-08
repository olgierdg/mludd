#!/usr/bin/python

import sqlite3 as sql
import pandas as pd
import dateutil.parser as date

def transform_to_data_frame(data):
    """Transforms a list of tuples to a pandas DataFrame object.
    
    Arguments:
    - `data`: data extracted from the database.
    """
    dict = {'timestamp': [],
            'latitude': [],
            'longitude': []}
    for (t, la, lo) in data:
        dict['timestamp'].append(date.parse(t))
        dict['latitude'].append(la)
        dict['longitude'].append(lo)
    return pd.DataFrame(dict)

def load_data_from_db(filename):
    """Loads data from sqlite3 database.
    Selects timestamp, latitude and longitude.
    Returns a pandas.DataFrame object.
    
    Arguments:
    - `filename`: name of the database file
    """
    conn = sql.connect(filename)
    cursor = conn.execute('SELECT timestamp,latitude,longitude FROM locationlog;')
    data = transform_to_data_frame(cursor.fetchall())
    conn.close()
    return data
