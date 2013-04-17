#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import scipy.interpolate as sp
import matplotlib.dates as md
import math
import datetime

def interpolate_data(data):
    t = data[:, 0]
    y = data[:, 1]
    t_t = []

    # zeby to sie dalo do funkcji wrzucic, robie z dat
    # integery, pominalem dzien, miesiac i rok, bo byly takie
    # same dla calego zestawu danych
#    for ti in t:
#        tt = ti.time()
#        t_t.append(tt.hour * 3600 + tt.minute * 60 + tt.second)   

    t_t = np.array(map(md.date2num, t))

    # to bylo potrzebne wczesniej, na wszelki wypadek zostawiam
    #indices = np.argsort(x)
    #x = x[indices]
    #y = y[indices]

    # s = 0 -> wtedy nie pomija punktow
    tck = sp.splrep(t_t, y, s = 0)

    # inne metody interpolacji
    #fl = sp.interp1d(t, y,kind='linear')
    #fc = sp.interp1d(t, y,kind='cubic')
    
    # tutaj sie tworzy nowy, "gestszy" czas. W zasadzie powinno byc co 1 min,
    # ale zrobilem duzego linspejsa co by pokazac, ze splajny dzialaja.
    #t_tnew = np.linspace(np.min(t_t), np.max(t_t), deltamins)

    # czas co 1 minute
    t_tnew = []
    ts = np.min(t_t)
    tm = np.max(t_t)
    while (ts <= tm):
        t_tnew.append(ts)
        ts = md.date2num(md.num2date(ts) + datetime.timedelta(minutes=1))

    ynew = sp.splev(t_tnew,tck,der=0)
    tnew = np.array(map(md.num2date, t_tnew))

    # wykres porownujacy
    #plt.figure()
    #plt.plot(t, y, 'ro', tnew, ynew)
    #plt.legend(['data', 'spline interpolation'], loc='lower right')
    #plt.xlabel("datetime")
    #plt.ylabel("latitude")
    #plt.show()

    newData = np.vstack([tnew,ynew])
    newData = np.transpose(newData)

    return newData

def interpolation(dataLat, dataLon):
    interpDataLat = interpolate_data(dataLat)
    interpDataLon = interpolate_data(dataLon)
    interpData = np.vstack([interpDataLat[:, 1], interpDataLon[:, 1]])
    interpData = np.transpose(interpData)
    return interpData
    
