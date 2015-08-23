#!/usr/bin/python2.7
from Crypto.Cipher import AES
import base64
import qrcode


class MyAES:
    def __init__(self):
	BLOCK_SIZE = 64
	PADDING = '|'
	secret = #key
	cipher = AES.new(secret)
	pad = lambda s: s + (BLOCK_SIZE-len(s) % BLOCK_SIZE)*PADDING
	self.encodeAES= lambda s: base64.b64encode(cipher.encrypt(pad(s)))
	self.decodeAES= lambda e: cipher.decrypt(base64.b64decode(e)).rstrip(PADDING)


    def encodeAES(self,data):
	encoded = self.encodeAES(data)
	return encoded

    def decodeAES(self,data):
	decoded= self.decodeAES(data)
	return decoded	
	



def main():
	input_file=open('./input','r')
	MyKey=MyAES()
	for line in input_file.readlines() :
		data=line.split(',')
		#data spliting
		enco =MyKey.encodeAES(data[0])
		print(enco)
		print("%s"%(MyKey.decodeAES(enco)))
		#data encrypting
		qr=qrcode.QRCode(version=1,error_correction=qrcode.constants.ERROR_CORRECT_M,box_size=10,border=4,)
		#initializing
		qr.add_data(enco)
		#add data for qrcode generating
		qr.make(fit=True)
		img=qr.make_image()
		#make qrcode
		file_name=data[0]
		file_name= file_name+".png"
		#make file name
		image_file = open(file_name,'w+')
		img.save(image_file,"PNG")
		#image file save
		image_file.close()
	input_file.close()

if __name__=="__main__":
	main()


