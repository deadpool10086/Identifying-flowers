# -*- coding: utf-8 -*-
from socket import *
from time import ctime
import threading
import hashlib
import Classification
from PIL import ImageFile
ImageFile.LOAD_TRUNCATED_IMAGES = True


host = ''
port = 8899
buffsize = 2048
ADDR = (host,port)

import Classification
def getResault(Client,n):
    data = tctimeClient.recv(buffsize);
    print(str(n))
    print("********************************************************************************")
    print(data)
    print("********************************************************************************")
    path = str(data).split(".")[0]
    houzhui = str(data).split(".")[1]
    print(path)
    print("********************************************************************************")
    print(houzhui)
    print("********************************************************************************")

    if houzhui[3] == '\\':
        houzhui = houzhui[0:3]
        #print("333333333333333333333333333333333333333333333")
        data = data[len(path)+3:]
    else:
        data = data[len(path)+4:]
        #print("444444444444444444444444444444444444444444444444")
        houzhui = houzhui[0:4]
    md5Count.update(path.encode("utf-8"))
    filename =  str(md5Count.hexdigest())  + '.' + houzhui
    print("********************************************************************************")
    print(data)
    print(filename)
    newFile = open(filename,'wb')
    flushSize = 0
    while True:
        newFile.write(data)
        flushSize += 1
        data = tctimeClient.recv(buffsize)
        if not data:
            break
        if data[-4:] == b"EOF!":
            break
        if (flushSize % 1000) == 0:
            newFile.flush()
    newFile.close()
    filename = './' + filename
    print(filename)
    Resault = Classification.classification(filename)
    tctimeClient.send(Resault.encode(encoding="utf-8"))
    tctimeClient.close()



if __name__=="__main__":
    tctime = socket(AF_INET,SOCK_STREAM)
    tctime.bind(ADDR)
    tctime.listen(3)
    md5Count = hashlib.md5()
    i = 0
    while True:
        i += 1
        print('Wait for connection ...')
        tctimeClient,addr = tctime.accept()
        print("Connection from :",addr)
        t = threading.Thread(target = getResault, args=(tctimeClient,i))
        t.start()
    tctimeClient.close()
