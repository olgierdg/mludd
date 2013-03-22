#!/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt
import scipy.interpolate as sp

def interpolate_data(data):
    t = data[:, 0]
    y = data[:, 1]
    t_t = []

    # zeby to sie dalo do funkcji wrzucic, robie z dat
    # integery, pominalem dzien, miesiac i rok, bo byly takie
    # same dla calego zestawu danych
    for ti in t:
        tt = ti.time()
        t_t.append(tt.hour * 3600 + tt.minute * 60 + tt.second)   

    # to bylo potrzebne wczesniej, na wszelki wypadek zostawiam
    #indices = np.argsort(x)
    #x = x[indices]
    #y = y[indices]

    tck = sp.splrep(t_t,y,s=1)

    # inne metody interpolacji
    #fl = sp.interp1d(t, y,kind='linear')
    #fc = sp.interp1d(t, y,kind='cubic')
    
    tnew = data[:, 0]
    ynew = sp.splev(t_t,tck,der=0)
    
    plt.figure()
    plt.plot(t,y,'o',tnew,ynew)
    #plt.legend(['data', 'spline interpolation'], loc='lower right')
    plt.xlabel("datetime")
    plt.ylabel("latitude")
    plt.show()
