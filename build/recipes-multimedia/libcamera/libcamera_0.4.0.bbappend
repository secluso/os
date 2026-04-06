# We need Raspberry Pi's patched version of libcamera to successfully use & build our rpicam-apps fork
# Thus, we override the default libcamera from recipes-multimedia's SRC_URI and SRCREV to use Raspberry Pi's patch
SRC_URI = "git://github.com/raspberrypi/libcamera.git;branch=main;protocol=https"
# Immutable stemming from 0.4.0+rpt20250213 [see https://github.com/raspberrypi/libcamera/releases/tag/v0.4.0%2Brpt20250213]
SRCREV = "29156679717bec7cc4784aeba3548807f2c27fca"
PV = "0.4.0+rpt20250213"