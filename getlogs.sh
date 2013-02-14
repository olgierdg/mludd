#!/bin/bash
sqlite3 $1 < getlogs.sql > k-means/logs.csv
