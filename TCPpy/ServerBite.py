from socket import *
from time import ctime
import threading
import hashlib

host = ''
port = 8899
buffsize = 2048
ADDR = (host,port)

def getResault(Client):
    data = str(tctimeClient.recv(buffsize));
    md5Count.update(data.encode("utf-8"))
    filename = ""
    filename +=  str(md5Count.hexdigest())
    houzhui = str(data).split(".")[-1]
    filename += "." + houzhui[0:-1]
    newFile = open(filename,'wb')
    flushSize = 0
    while True:
        data = tctimeClient.recv(buffsize)
        if not data:
            print("i came out of nodata")
            break
        if data[-4:] == b"EOF!":
            print("i came out of EOF")
            break
        newFile.write(data)
        flushSize += 1
        print("count"+str(flushSize))
        #print flushSize
        if (flushSize % 1000) == 0:
            newFile.flush()
        print(data[0:10])
    #print(data)
    print("i'm send ")
    tctimeClient.send(b"asdasdssssssssssss")
    print("i'm sended ")
    tctimeClient.close()
    newFile.close()


if __name__=="__main__":
    tctime = socket(AF_INET,SOCK_STREAM)
    tctime.bind(ADDR)
    tctime.listen(3)
    md5Count = hashlib.md5()
    for i in range(1,10):
        print('Wait for connection ...')
        tctimeClient,addr = tctime.accept()
        print("Connection from :",addr)
        t = threading.Thread(target = getResault, args=(tctimeClient,))
        t.start()
    tctimeClient.close()
