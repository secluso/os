# SPDX-License-Identifier: MIT
#
# Copied verbatim from meta-yocto at the yocto-5.3.3 (whinlatter) tag, commit 9b98ec5e6cfb6c9dc74d3c2e6304eeb274f7c487:
# https://git.yoctoproject.org/meta-yocto/tree/meta-poky/conf/distro/include/gcsections.inc?h=yocto-5.3.3
#
# The original file in meta-poky is MIT-licensed under the OpenEmbedded license (meta-yocto's COPYING.MIT).
# Thus, it is kept under that license here.

CFLAGS_SECTION_REMOVAL = "-ffunction-sections -fdata-sections"
LDFLAGS_SECTION_REMOVAL = "-Wl,--gc-sections"

# packages with build problems using sections
CFLAGS_SECTION_REMOVAL:pn-glibc = ""
LDFLAGS_SECTION_REMOVAL:pn-glibc = ""
CFLAGS_SECTION_REMOVAL:pn-cairo = ""
LDFLAGS_SECTION_REMOVAL:pn-cairo = ""
CFLAGS_SECTION_REMOVAL:pn-perl = ""
LDFLAGS_SECTION_REMOVAL:pn-perl = ""
CFLAGS_SECTION_REMOVAL:pn-grub-efi = ""
LDFLAGS_SECTION_REMOVAL:pn-grub-efi = ""
CFLAGS_SECTION_REMOVAL:pn-grub = ""
LDFLAGS_SECTION_REMOVAL:pn-grub = ""

# SDK packages with build problems using sections
CFLAGS_SECTION_REMOVAL:pn-nativesdk-glibc = ""
LDFLAGS_SECTION_REMOVAL:pn-nativesdk-glibc = ""
CFLAGS_SECTION_REMOVAL:pn-nativesdk-cairo = ""
LDFLAGS_SECTION_REMOVAL:pn-nativesdk-cairo = ""
CFLAGS_SECTION_REMOVAL:pn-nativesdk-mingw-w64-runtime = ""
LDFLAGS_SECTION_REMOVAL:pn-nativesdk-mingw-w64-runtime = ""
CFLAGS_SECTION_REMOVAL:pn-nativesdk-perl = ""
LDFLAGS_SECTION_REMOVAL:pn-nativesdk-perl = ""
CFLAGS_SECTION_REMOVAL:pn-nativesdk-mingw-w64-winpthreads = ""
LDFLAGS_SECTION_REMOVAL:pn-nativesdk-mingw-w64-winpthreads = ""

# set default for target
CFLAGS:append:class-target = " ${CFLAGS_SECTION_REMOVAL}"
CXXFLAGS:append:class-target = " ${CFLAGS_SECTION_REMOVAL}"
LDFLAGS:append:class-target = " ${LDFLAGS_SECTION_REMOVAL}"

# set default for nativesdk
CFLAGS:append:class-nativesdk = " ${CFLAGS_SECTION_REMOVAL}"
CXXFLAGS:append:class-nativesdk = " ${CFLAGS_SECTION_REMOVAL}"
LDFLAGS:append:class-nativesdk = " ${LDFLAGS_SECTION_REMOVAL}"
