#!/usr/bin/python2.6
# -*- coding: utf-8 -*-
from aip import AipImageClassify

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
