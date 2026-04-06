# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

#TODO: Verify reproducible

# Referenced https://docs.yoctoproject.org/dev-manual/new-recipe.html
# Referenced https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore

# Note: We use a custom recipe here instead of the official rpicam-apps as we have efficiency structuring to fetch the latest frame

SUMMARY = "A suite of libcamera-based apps for the Raspberry Pi"
DESCRIPTION = "This is a small suite of libcamera-based apps that aim to \
copy the functionality of the existing \"raspicam\" apps."

# Home page of rpicam-apps: https://github.com/raspberrypi/rpicam-apps
HOMEPAGE = "https://github.com/seclusi/rpicam-apps-fork"
SECTION = "console/utils"

# This is the native license from their repository.
LICENSE = "BSD-2-Clause & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://license.txt;md5=a0013d1b383d72ba4bdc5b750e7d1d77 \
file://LICENSE.secluso;md5=6d4ff442c842d1ba9eeb1a2a236c37a4"

# This is our own repository for our fork (set to an immutable commit)
SRC_URI = "git://github.com/secluso/rpicam-apps-fork.git;branch=main;protocol=https"
SRCREV = "13d79600eb0f9f510c39055f0e683337a8a13698"

# Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
S = "${WORKDIR}/git"

# Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
# Note: We set libcamera to 0.4.0 under PREFERRED_VERSION in the kas .yml in local_conf_header
DEPENDS = "libcamera libexif jpeg tiff libpng boost"

# Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
inherit meson pkgconfig

# Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DBoost_INCLUDE_DIR=${STAGING_INCDIR} \
    -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR} \
"

# This is all the files we're keeping from this recipe.
# Note: rpicam-* are symlinks to libcamera-*
FILES:${PN} = "\
  /usr/lib/rpicam_app.so.1.6.0 \
  /usr/bin/rpicam-jpeg \
  /usr/bin/rpicam-raw \
  /usr/bin/rpicam-vid \
  /usr/bin/rpicam-hello \
  /usr/bin/rpicam-still \
  /usr/bin/libcamera-hello \
  /usr/bin/libcamera-still \
  /usr/bin/libcamera-jpeg \
  /usr/bin/libcamera-raw \
  /usr/bin/libcamera-vid \
"

# Some of these are automatic. We disable them to be safe to prevent extra bloat.
EXTRA_OEMESON += "-Denable_libav=disabled \
          -Denable_drm=disabled \
          -Denable_egl=disabled \
          -Denable_qt=disabled \
          -Denable_opencv=disabled \
          -Denable_tflite=disabled \
          -Denable_hailo=disabled \
          -Ddownload_hailo_models=false \
          "

# Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
LIBCAMERA_ARCH = "${TARGET_ARCH}"
LIBCAMERA_ARCH:aarch64 = "arm64"
LIBCAMERA_ARCH:arm = "armv8-neon"
EXTRA_OECMAKE += "-DENABLE_COMPILE_FLAGS_FOR_TARGET=${LIBCAMERA_ARCH}"

do_install:append() {
    # Taken from https://git.yoctoproject.org/meta-raspberrypi/tree/recipes-multimedia/libcamera-apps/rpi-libcamera-apps_git.bb?h=mickledore
    # Requires python3-core which not all systems may have
    rm -v ${D}/${bindir}/camera-bug-report

    # ${D} = staging destination directory
    # ${datadir} = /usr/share
    # ${execprefix} = /usr
    # We get rid of these due to not needing them in Secluso.
    # We cannot exclude them automatically with a flag in rpicam-apps.
    rm -vrf ${D}${exec_prefix}/lib/rpicam-apps-postproc
    rm -vrf ${D}${datadir}/rpi-camera-assets
    rm -vrf ${D}${datadir}

    # We cannot have an un-versioned .so file without having a -dev prefix as it's a development symlink
    rm -v ${D}${libdir}/rpicam_app.so
}
