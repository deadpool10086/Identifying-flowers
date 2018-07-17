#!/usr/bin/env python3
#-*- coding:utf-8 -*-

from socket import *
from time import ctime

host = ''
port = 8899
buffsize = 2048
ADDR = (host,port)

tctime = socket(AF_INET,SOCK_STREAM)
tctime.bind(ADDR)
tctime.listen(3)

for i in range(1,10):
    print('Wait for connection ...')
    tctimeClient,addr = tctime.accept()
    print("Connection from :",addr)

    while True:
        data = tctimeClient.recv(buffsize).decode()
        print(data);
        if not data:
            break
    #     tctimeClient.send(('[%s] %s' % (ctime(),data)).encode())
    tctimeClient.close()
tctimeClient.close()
