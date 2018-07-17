#!/usr/bin/python2.6
# -*- coding: utf-8 -*-
from aip import AipImageClassify
import socket

address = ('', 8080)
receive = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
receive.bind(address)
newFile = open("example.jpg",'wb')
flushSize = 0
while 1:
    data, addr = receive.recvfrom(50 * 1024)
    if not data:
        break
    if data == "exit data mark":
        receive.sendto("exit data mark",addr)
        break
    newFile.write(data)
    flushSize += 1
    print flushSize
    if (flushSize % 1000) == 0:
        newFile.flush()
    receive.sendto("success data mark", addr)
newFile.close()

""" 你的 APPID AK SK """
APP_ID = '10441770'
API_KEY = 'ZhUS1Ht4WVmGnhndUwSDOz48'
SECRET_KEY = 'qclYK2ky9bf9a5Uhh1gLM7hPcXs4TEjw'

aipImageClassify = AipImageClassify(APP_ID, API_KEY, SECRET_KEY)

def get_file_content(filePath):
    with open(filePath, 'rb') as fp:
        return fp.read()

image = get_file_content('example.jpg')

""" 调用植物识别 """
xRes = aipImageClassify.plantDetect(image);
print xRes['result'][0]['name']
