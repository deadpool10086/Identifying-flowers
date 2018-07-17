#!/usr/bin/env python3
#-*- coding:utf-8 -*-

from socket import *

HOST ='localhost'

PORT = 12345

BUFFSIZE=2048

ADDR = (HOST,PORT)

tctimeClient = socket(AF_INET,SOCK_STREAM)

tctimeClient.connect(ADDR)

for i in range(1,10):
    data = "qwrqweqwf"
    if not data:
        break
    tctimeClient.send(data.encode())
    data = tctimeClient.recv(BUFFSIZE).decode()
    if not data:
        break
    print(data)
tctimeClient.close()
