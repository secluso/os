# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

FILESEXTRAPATHS:prepend := "${THISDIR}:"

# We need Raspberry Pi's patched version of libcamera to successfully use & build our rpicam-apps fork
# Thus, we override the default libcamera from recipes-multimedia's SRC_URI and SRCREV to use Raspberry Pi's patch
SRC_URI = "git://github.com/raspberrypi/libcamera.git;branch=main;protocol=https"
# Immutable stemming from 0.5.2+rpt20250903 [see https://github.com/raspberrypi/libcamera/releases/tag/v0.5.2%2Brpt20250903]
SRCREV = "bfd68f786964636b09f8122e6c09c230367390e7"
PV = "0.5.2+rpt20250903"

SRC_URI = " \
    git://github.com/raspberrypi/libcamera.git;protocol=https;branch=main;tag=v${PV} \
    file://0001-libcamera-Do-not-assume-libc-with-clang.patch \
    # libcamera's meson.build uses utils/gen-version.sh to generate a build-time version string.
    file://0002-libcamera-Make-dirty-version-suffix-reproducible.patch \
    # utils/gen-ipa-priv-key.sh generates a fresh RSA key for every build (which is embedded into libcamera.so)
    file://fixed-ipa-priv-key.pem \
"

do_configure:append() {
        install -m 0600 ${UNPACKDIR}/fixed-ipa-priv-key.pem ${S}/fixed-ipa-priv-key.pem

        # libcamera generates a fresh IPA signing key for every build (which is embedded into libcamera.so as a public key)
        sed -i 's#openssl genpkey -algorithm RSA -out "${key}" -pkeyopt rsa_keygen_bits:2048#cp "$(dirname "$0")/../fixed-ipa-priv-key.pem" "${key}"#' \
                ${S}/utils/gen-ipa-priv-key.sh
}