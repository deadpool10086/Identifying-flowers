# coding: UTF-8
testFile = open('temp.txt',"wb")
try:
    testFile.write("你好世界")
    testFile.flush();
    testFile.write("爱232323地")
finally:
    testFile.close()