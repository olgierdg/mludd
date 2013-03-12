#!/usr/bin/python

import sqlite3 as sql
import pandas as pd
import dateutil.parser as date

def load_data_from_db(filename):
    """Loads data from sqlite3 database.
    Selects timestamp, latitude and longitude.
    Returns a pandas.DataFrame object.
    
    Arguments:
    - `filename`: name of the database file
    """
    conn = sql.connect(filename)
    cursor = conn.execute('SELECT timestamp,latitude,longitude FROM locationlog;')
    
    dict = {'timestamp': [],
            'latitude': [],
            'longitude': []}
    for (t, la, lo) in cursor.fetchall():
        dict['timestamp'].append(date.parse(t))
        dict['latitude'].append(la)
        dict['longitude'].append(lo)
    
    conn.close()
    return pd.DataFrame(dict)
