# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

#TODO: Verify reproducible

# Referenced https://docs.yoctoproject.org/dev-manual/new-recipe.html
# Referenced https://github.com/facebook/openbmc/blob/5bf12b96fd7797c84798049bc404581be8390bc7/meta-facebook/meta-catalina/recipes-catalina/satellite-relay/satellite-relay_0.1.0.bb

# Inheriting cargo is what will build the crate here.
inherit cargo cargo-update-recipe-crates systemd

SUMMARY = "Recipe to build and install Secluso Camera Hub"
HOMEPAGE = "https://github.com/secluo/core"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b769fddc23425484f6d001e49426c2ee"

# This is our own repository (set to an immutable commit)
SRC_URI = "git://github.com/secluso/core.git;branch=remove-openssl-from-camera-hub;protocol=https"
SRCREV = "91cfa054e343b1119f580a4dede56a612c7fb1f9"
DEPENDS += " onnxruntime"
# In meta-rust, it shows we can override CARGO_SRC_DIR to specify our intended source directory within repository [https://github.com/meta-rust/meta-rust/blob/master/classes/cargo.bbclass]
CARGO_SRC_DIR = "camera_hub"
CARGO_BUILD_FLAGS += "--features raspberry"

# The binary gets installed in /usr/bin/secluso-camera-hub per https://github.com/meta-rust/meta-rust/blob/328334d9d31241d1d29eb754c5c102d5b0e002ab/classes/rust-bin.bbclass#L5


# https://wiki.koansoftware.com/index.php/Add_a_systemd_service_file_into_a_Yocto_image
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "secluso_camera_hub.service"
SRC_URI:append = " file://secluso_camera_hub.service "
FILES:${PN} += "${systemd_unitdir}/system/secluso_camera_hub.service"
RDEPENDS:${PN} += "systemd"

do_install() {
  install -d ${D}/${systemd_unitdir}/system
  install -m 0644 ${UNPACKDIR}/secluso_camera_hub.service ${D}/${systemd_unitdir}/system
}

# https://docs.yoctoproject.org/dev/ref-manual/classes.html#cargo-update-recipe-crates
# Generate new one: `bitbake -c update_crates secluso-camera-hub` from project root
require ${BPN}-crates.inc
