ML User Domain Discovery
========================

python
------
Install pip:

    sudo apt-get install python-pip

Install numpy and scipy:

    sudo pip install numpy blas scipy matplotlib dateutil pandas

Alternatively, without using pip:

    sudo apt-get install python-pandas python-numpy python-scipy python-matplotlib python-dateutil

### parsing csv

    reader = csv.csvreader(csvfile)
    result = np.array([[magic(col) for col in row] for row in reader])

    def magic(s):
        if '/' in s:
            return datetime(s)
        elif '.' in s:
            return float(s)
        else:
            return int(s)

### matplotlib dates as numbers
Maybe matplotlib has support for datetime objects?
(Link)[http://stackoverflow.com/questions/7513262/matplotlib-large-set-of-colors-for-plots]
