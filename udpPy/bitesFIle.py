#!/usr/bin/python2.6
# -*- coding: utf-8 -*-
from aip import AipImageClassify
import socket
import threading


class LoopThread(threading._Timer):
    def run(self):
        while True:
            self.finished.wait(self.interval)
            if not self.finished.is_set():
                self.function(*self.args, **self.kwargs)
            else:
                self.finished.set()
                break

def resent(receive,addr):
    receive.sendto("time out data",addr)
    print addr
    print "re sent"

address = ('', 4567)
receive = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
receive.bind(address)

flushSize = 0
i = False
# sss = LoopThread(0.5, resent, (receive,addr))
#sss.start()
while True:
    newFile = open("temp.jpg", 'wb')
    while 1:
        # sss.run()
        data, addr = receive.recvfrom(50 * 1024)
        # sss.cancel()
        i = True
        if not data:
            print "tuichu"
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
    APP_ID = '10441770'
    API_KEY = 'ZhUS1Ht4WVmGnhndUwSDOz48'
    SECRET_KEY = 'qclYK2ky9bf9a5Uhh1gLM7hPcXs4TEjw'

    aipImageClassify = AipImageClassify(APP_ID, API_KEY, SECRET_KEY)


    def get_file_content(filePath):
        with open(filePath, 'rb') as fp:
            return fp.read()


    image = get_file_content('temp.jpg')

    """ 调用植物识别 """
    xRes = aipImageClassify.plantDetect(image);
    print xRes['result'][0]['name']
    receive.sendto(xRes['result'][0]['name'].encode('UTF-8'),addr)



