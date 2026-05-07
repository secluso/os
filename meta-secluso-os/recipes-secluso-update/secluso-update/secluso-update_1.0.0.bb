# SPDX-License-Identifier: GPL-3.0-only
# Copyright (C) 2026 Secluso, Inc.
# Additional terms apply; see the NOTICE file in the repository root.

# Note: See secluso-camera-hub_1.0.0.bb for references on this file.

inherit cargo cargo-update-recipe-crates systemd

SUMMARY = "Recipe to build and install the Secluso OS updater"
HOMEPAGE = "https://github.com/secluso/core"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b769fddc23425484f6d001e49426c2ee"

SRC_URI = " \
    git://github.com/secluso/core.git;branch=remove-openssl-from-camera-hub;protocol=https \
    file://secluso_update.service \
"
SRCREV = "183642cc858c0e0e1b5a70a13992ae367dc67351"

CARGO_SRC_DIR = "update"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "secluso_update.service"

RDEPENDS:${PN} += " \
    ca-certificates \
    secluso-camera-hub \
    systemd \
"

FILES:${PN} += " \
    ${systemd_unitdir}/system/secluso_update.service \
"

do_install:append() {
    install -d ${D}${localstatedir}/lib/secluso
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/secluso_update.service ${D}${systemd_unitdir}/system
}

# Regenerate after dependency changes with:
# Generate new one: `bitbake -c update_crates secluso-update` from project root
require ${BPN}-crates.inc

