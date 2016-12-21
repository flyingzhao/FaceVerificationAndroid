#Face Verification on Android

Face verification with mxnet on android

##Face Verification

I use pre-trained LightenedCNN as feature extractor. 

![arch](http://img.blog.csdn.net/20161112165845008)

For more information please read the original paper

>https://arxiv.org/abs/1511.02683

My notes about this paper: [Lightened CNN](http://blog.csdn.net/tinyzhao/article/details/53127870)

The similarity metric is cosine similarity. The threshold should be about 0.36 ,and it should be adjusted according to specific applications.

##Face Detection

OpenCV has an in-built Viola-Jones face detector which is used here to detect faces.

##MXNet

I have compiled mxnet for android with ndk-r13b. You can alse refer to my blog for more details.

>http://blog.csdn.net/tinyzhao/article/details/53288102

You can also download compiled libmxnet_predict.so [here](https://github.com/flyingzhao/FaceVerificationAndroid/blob/master/app/src/main/jniLibs/armeabi/libmxnet_predict.so)

##Result

I test on my Xiaomi 4C (Snapdragon 808) and I get about 0.8s per image.
