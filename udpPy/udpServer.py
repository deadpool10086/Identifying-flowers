# coding: UTF-8
import socket

address = ('', 8080)
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind(address)

while True:
    data, addr = s.recvfrom(2048)
    if not data:
        print "client has exist"
        break
    print "received:", data, "from", addr
    s.sendto("你好我是python Server",addr)
s.close()
