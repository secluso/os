# We need Raspberry Pi's patched version of libcamera to successfully use & build our rpicam-apps fork
# Thus, we override the default libcamera from recipes-multimedia's SRC_URI and SRCREV to use Raspberry Pi's patch
SRC_URI = "git://github.com/raspberrypi/libcamera.git;branch=main;protocol=https"
# Immutable stemming from 0.5.2+rpt20250903 [see https://github.com/raspberrypi/libcamera/releases/tag/v0.5.2%2Brpt20250903]
SRCREV = "bfd68f786964636b09f8122e6c09c230367390e7"
PV = "0.5.2+rpt20250903"